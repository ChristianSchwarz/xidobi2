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
#include <jni.h>
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
	handle = CreateFile(fileName,
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

	HANDLE handle = CreateEventA(	(LPSECURITY_ATTRIBUTES) lpEventAttributes,
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

	DWORD bytesWritten;
	OVERLAPPED overlapped = {0};

	getOVERLAPPEDFields(env, lpOverlapped, &overlapped);

	jbyte* jBuffer = (*env)->GetByteArrayElements(env, lpBuffer, NULL);

	BOOL result = WriteFile( (HANDLE) handle,
							 jBuffer,
							 (DWORD) nNumberOfBytesToWrite,
							 &bytesWritten,
							 &overlapped);

	setINT(env, lpNumberOfBytesWritten, &bytesWritten);
	setOVERLAPPEDFields(env, lpOverlapped, &overlapped);

	(*env)->ReleaseByteArrayElements(env, lpBuffer, jBuffer, 0);

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
	DWORD error = GetLastError();
	return (jint) error;
}

/*
 * Class:     org_xidobi_OS
 * Method:    GetOverlappedResult
 * Signature: (ILorg/xidobi/OVERLAPPED;Lorg/xidobi/INT;Z)Z
 */
JNIEXPORT jboolean JNICALL Java_org_xidobi_OS_GetOverlappedResult(JNIEnv * env, jclass clazz,
		  jint handle,
		  jobject lpOverlapped,
		  jobject lpNumberOfBytesTransferred,
		  jboolean bWait){

	OVERLAPPED overlapped={0};
	getOVERLAPPEDFields(env,lpOverlapped, &overlapped);
	DWORD written=0;

	BOOL result = GetOverlappedResult((HANDLE) handle,
									  &overlapped,
										&written,
										(BOOL)bWait);

	setINT(env,lpNumberOfBytesTransferred,&written);

	if (result==TRUE)
		return JNI_TRUE;
	return JNI_FALSE;
}
