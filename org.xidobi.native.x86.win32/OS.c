/*
 * OS.c
 *
 * Copyright 2013 Gemtec GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
#include "jni.h"
#include <stdlib.h>
#include <windows.h>

#include "OS_structs.h"
#include "org_xidobi_OS.h"

/*
 * Class:     org_xidobi_OS
 * Method:    CreateFile
 * Signature: (Ljava/lang/String;IIIIII)I
 */
JNIEXPORT jint JNICALL
Java_org_xidobi_OS_CreateFile(JNIEnv *env, jclass clazz,
		jstring lpFileName,
		jint dwDesiredAccess,
		jint dwShareMode,
		jint lpSecurityAttributes,
		jint dwCreationDisposition,
		jint dwFlagsAndAttributes,
		jint hTemplateFile) {

	const char* fileName = (*env)->GetStringUTFChars(env, lpFileName, NULL);

	HANDLE handle;
	handle = CreateFileA(fileName,
						dwDesiredAccess,
						dwShareMode,
						(LPSECURITY_ATTRIBUTES) lpSecurityAttributes,
						dwCreationDisposition,
						dwFlagsAndAttributes,
						(HANDLE) hTemplateFile);

	(*env)->ReleaseStringUTFChars(env, lpFileName, fileName);

	return (jint) handle;
}

/*
 * Class:     org_xidobi_OS
 * Method:    CloseHandle
 * Signature: (I)Z
 */
JNIEXPORT jboolean JNICALL
Java_org_xidobi_OS_CloseHandle(JNIEnv *env, jclass clazz,
		jint handle) {
	if (CloseHandle((HANDLE) handle))
		return JNI_TRUE;
	return JNI_FALSE;
}


/*
 * Class:     org_xidobi_OS
 * Method:    GetCommState
 * Signature: (ILorg/xidobi/DCB;)Z
 */
JNIEXPORT jboolean JNICALL
Java_org_xidobi_OS_GetCommState(JNIEnv *env, jclass clazz,
		jint handle,
		jobject dcbObject) {
	DCB dcb;
	FillMemory(&dcb, sizeof(dcb), 0);

	if (!GetCommState((HANDLE) handle, &dcb))
		return JNI_FALSE;
	setDCBFields(env, dcbObject, &dcb);

	return JNI_TRUE;
}


/*
 * Class:     org_xidobi_OS
 * Method:    SetCommState
 * Signature: (ILorg/xidobi/DCB;)Z
 */
JNIEXPORT jboolean JNICALL
Java_org_xidobi_OS_SetCommState(JNIEnv *env, jclass clazz,
		jint handle,
		jobject dcbObject) {
	DCB dcb;
	getDCBFields(env, dcbObject, &dcb);

	if (!SetCommState((HANDLE) handle, &dcb))
		return JNI_FALSE;

	return JNI_TRUE;
}


/*
 * Class:     org_xidobi_OS
 * Method:    CreateEventA
 * Signature: (IZZLjava/lang/String;)I
 */
JNIEXPORT jint JNICALL
Java_org_xidobi_OS_CreateEventA(JNIEnv *env, jclass clazz,
		jint lpEventAttributes,
		jboolean bManualReset,
		jboolean bInitialState,
		jstring lpName) {

	const char* name;
	if (lpName == NULL)
		name = NULL;
	else
		name = (*env)->GetStringUTFChars(env, lpName, NULL);

	HANDLE handle = CreateEventA(	NULL,
									bManualReset,
									bInitialState,
									name) ;

	if (name != NULL)
		(*env)->ReleaseStringUTFChars(env, lpName, name);

	return (jint) handle;
}


/*
 * Class:     org_xidobi_OS
 * Method:    WriteFile
 * Signature: (I[BILorg/xidobi/INT;Lorg/xidobi/OVERLAPPED;)Z
 */
JNIEXPORT jboolean JNICALL
Java_org_xidobi_OS_WriteFile(JNIEnv *env, jclass clazz,
		jint handle,
		jbyteArray lpBuffer,
		jint nNumberOfBytesToWrite,
		jobject lpNumberOfBytesWritten,
		jobject lpOverlapped) {

	DWORD bytesWritten = 0;
	OVERLAPPED *overlapped = getOVERLAPPED(env, lpOverlapped);

	jbyte* jBuffer = (*env)->GetByteArrayElements(env, lpBuffer, NULL);

	BOOL result = WriteFile( (HANDLE) handle,
							 jBuffer,
							 (DWORD) nNumberOfBytesToWrite,
							 &bytesWritten,
							 overlapped);

	setINT(env, lpNumberOfBytesWritten, &bytesWritten);
	setOVERLAPPED(env, lpOverlapped, overlapped);

	(*env)->ReleaseByteArrayElements(env, lpBuffer, jBuffer, 0);

	if (result)
		return JNI_TRUE;
	return JNI_FALSE;
}

/*
 * Class:     org_xidobi_OS
 * Method:    ReadFile
 * Signature: (I[BILorg/xidobi/structs/INT;Lorg/xidobi/structs/OVERLAPPED;)Z
 */
JNIEXPORT jboolean JNICALL
Java_org_xidobi_OS_ReadFile(JNIEnv *env, jclass clazz,
		jint handle,
		jbyteArray lpBuffer,
		jint nNumberOfBytesToRead,
		jobject lpNumberOfBytesRead,
		jobject lpOverlapped) {

	DWORD bytesRead = 0;
	OVERLAPPED *overlapped = getOVERLAPPED(env, lpOverlapped);

	jsize size = (*env)->GetArrayLength(env, lpBuffer);
	const jbyte jBuffer[size];

	BOOL result = ReadFile( (HANDLE) handle,
							 &jBuffer,
							 (DWORD) nNumberOfBytesToRead,
							 &bytesRead,
							 overlapped);

	setINT(env, lpNumberOfBytesRead, &bytesRead);
	setOVERLAPPED(env, lpOverlapped, overlapped);

	(*env)->SetByteArrayRegion(env, lpBuffer, 0, bytesRead, jBuffer);

	if (result)
		return JNI_TRUE;
	return JNI_FALSE;
}

/*
 * Class:     org_xidobi_OS
 * Method:    GetLastError
 * Signature: ()I
 */
JNIEXPORT jint JNICALL
Java_org_xidobi_OS_GetLastError(JNIEnv *env, jclass clazz) {
	return (jint) GetLastError();
}

/*
 * Class:     org_xidobi_OS
 * Method:    GetOverlappedResult
 * Signature: (ILorg/xidobi/OVERLAPPED;Lorg/xidobi/INT;Z)Z
 */
JNIEXPORT jboolean JNICALL
Java_org_xidobi_OS_GetOverlappedResult(JNIEnv * env, jclass clazz,
		  jint handle,
		  jobject lpOverlapped,
		  jobject lpNumberOfBytesTransferred,
		  jboolean bWait){

	DWORD written = 0;

	OVERLAPPED *overlapped = getOVERLAPPED(env, lpOverlapped);

	BOOL result = GetOverlappedResult((HANDLE) handle,
									  overlapped,
									  &written,
									  (BOOL) bWait);

	setINT(env, lpNumberOfBytesTransferred, &written);
	setOVERLAPPED(env, lpOverlapped, overlapped);

	if (result)
		return JNI_TRUE;
	return JNI_FALSE;
}

/*
 * Class:     org_xidobi_OS
 * Method:    WaitForSingleObject
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL
Java_org_xidobi_OS_WaitForSingleObject(JNIEnv *env, jclass clazz,
		jint hhandle,
		jint dwMilliseconds) {
	DWORD error = WaitForSingleObject(	(HANDLE) hhandle,
										dwMilliseconds);
	return (jint) error;
}

/*
 * Class:     org_xidobi_OS
 * Method:    malloc
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL
Java_org_xidobi_OS_malloc(JNIEnv *env, jclass clazz,
		jint size) {
	return (jint) malloc(size);
}

/*
 * Class:     org_xidobi_OS
 * Method:    free
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_org_xidobi_OS_free(JNIEnv *env, jclass clazz,
		jint pointer) {
	free((void *) pointer);
}

/*
 * Class:     org_xidobi_OS
 * Method:    sizeOf_OVERLAPPED
 * Signature: ()I
 */
JNIEXPORT jint JNICALL
Java_org_xidobi_OS_sizeOf_1OVERLAPPED(JNIEnv *env, jclass clazz) {
	return (jint) sizeof(OVERLAPPED);
}

