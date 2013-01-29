/*
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
#include <stdlib.h>
#include <windows.h>

#include "jni.h"

#include "OS_structs.h"

// ***********************************************************************
// **** DCB: *************************************************************
// ***********************************************************************

/*
 * a struct to cache the DCB fields
 */
typedef struct DCB_FID_CACHE {
	int cached;
	jclass clazz;
	jfieldID BaudRate, ByteSize, DCBlength, EofChar, ErrorChar, EvtChar, Parity,
			StopBits, XoffChar, XoffLim, XonChar, XonLim, fAbortOnError,
			fBinary, fDsrSensitivity, fDtrControl, fDummy2, fErrorChar, fInX,
			fNull, fOutX, fOutxCtsFlow, fOutxDsrFlow, fParity, fRtsControl,
			fTXContinueOnXoff, wReserved, wReserved1;
} DCB_FID_CACHE;
// cache for DCB fields
DCB_FID_CACHE DCBc;

/*
 * Caches the DCB fields in the DCB_FID_CACHE.
 */
void cacheDCBFields(JNIEnv *env, jobject dcbObject) {
	if (DCBc.cached)
		return;

	DCBc.clazz = (*env)->GetObjectClass(env, dcbObject);
	DCBc.BaudRate = (*env)->GetFieldID(env, DCBc.clazz, "BaudRate", "I");
	DCBc.ByteSize = (*env)->GetFieldID(env, DCBc.clazz, "ByteSize", "B");
	DCBc.DCBlength = (*env)->GetFieldID(env, DCBc.clazz, "DCBlength", "I");
	DCBc.EofChar = (*env)->GetFieldID(env, DCBc.clazz, "EofChar", "C");
	DCBc.ErrorChar = (*env)->GetFieldID(env, DCBc.clazz, "ErrorChar", "C");
	DCBc.EvtChar = (*env)->GetFieldID(env, DCBc.clazz, "EvtChar", "C");
	DCBc.Parity = (*env)->GetFieldID(env, DCBc.clazz, "Parity", "B");
	DCBc.StopBits = (*env)->GetFieldID(env, DCBc.clazz, "StopBits", "B");
	DCBc.XoffChar = (*env)->GetFieldID(env, DCBc.clazz, "XoffChar", "C");
	DCBc.XoffLim = (*env)->GetFieldID(env, DCBc.clazz, "XoffLim", "S");
	DCBc.XonChar = (*env)->GetFieldID(env, DCBc.clazz, "XonChar", "C");
	DCBc.XonLim = (*env)->GetFieldID(env, DCBc.clazz, "XonLim", "S");
	DCBc.fAbortOnError = (*env)->GetFieldID(env, DCBc.clazz, "fAbortOnError", "I");
	DCBc.fBinary = (*env)->GetFieldID(env, DCBc.clazz, "fBinary", "I");
	DCBc.fDsrSensitivity = (*env)->GetFieldID(env, DCBc.clazz, "fDsrSensitivity", "I");
	DCBc.fDtrControl = (*env)->GetFieldID(env, DCBc.clazz, "fDtrControl", "I");
	DCBc.fDummy2 = (*env)->GetFieldID(env, DCBc.clazz, "fDummy2", "I");
	DCBc.fErrorChar = (*env)->GetFieldID(env, DCBc.clazz, "fErrorChar", "I");
	DCBc.fInX = (*env)->GetFieldID(env, DCBc.clazz, "fInX", "I");
	DCBc.fNull = (*env)->GetFieldID(env, DCBc.clazz, "fNull", "I");
	DCBc.fOutX = (*env)->GetFieldID(env, DCBc.clazz, "fOutX", "I");
	DCBc.fOutxCtsFlow = (*env)->GetFieldID(env, DCBc.clazz, "fOutxCtsFlow", "I");
	DCBc.fOutxDsrFlow = (*env)->GetFieldID(env, DCBc.clazz, "fOutxDsrFlow", "I");
	DCBc.fParity = (*env)->GetFieldID(env, DCBc.clazz, "fParity", "I");
	DCBc.fRtsControl = (*env)->GetFieldID(env, DCBc.clazz, "fRtsControl", "I");
	DCBc.fTXContinueOnXoff = (*env)->GetFieldID(env, DCBc.clazz, "fTXContinueOnXoff", "I");
	DCBc.wReserved = (*env)->GetFieldID(env, DCBc.clazz, "wReserved", "S");
	DCBc.wReserved1 = (*env)->GetFieldID(env, DCBc.clazz, "wReserved1", "S");

	DCBc.cached = TRUE;
}

/*
 * Retrieves the fields from the given jobject and returns it as DCB*.
 */
DCB *getDCBFields(JNIEnv *env, jobject dcbObject, DCB *dcbStruct) {
	if (!DCBc.cached)
		cacheDCBFields(env, dcbObject);

	dcbStruct->BaudRate = (*env)->GetIntField(env, dcbObject, DCBc.BaudRate);
	dcbStruct->ByteSize = (*env)->GetByteField(env, dcbObject, DCBc.ByteSize);
	dcbStruct->DCBlength = (*env)->GetIntField(env, dcbObject, DCBc.DCBlength);
	dcbStruct->EofChar = (*env)->GetCharField(env, dcbObject, DCBc.EofChar);
	dcbStruct->ErrorChar = (*env)->GetCharField(env, dcbObject, DCBc.ErrorChar);
	dcbStruct->EvtChar = (*env)->GetCharField(env, dcbObject, DCBc.EvtChar);
	dcbStruct->Parity = (*env)->GetByteField(env, dcbObject, DCBc.Parity);
	dcbStruct->StopBits = (*env)->GetByteField(env, dcbObject, DCBc.StopBits);
	dcbStruct->XoffChar = (*env)->GetCharField(env, dcbObject, DCBc.XoffChar);
	dcbStruct->XoffLim = (*env)->GetShortField(env, dcbObject, DCBc.XoffLim);
	dcbStruct->XonChar = (*env)->GetCharField(env, dcbObject, DCBc.XonChar);
	dcbStruct->XonLim = (*env)->GetShortField(env, dcbObject, DCBc.XonLim);
	dcbStruct->fAbortOnError = (*env)->GetIntField(env, dcbObject, DCBc.fAbortOnError);
	dcbStruct->fBinary = (*env)->GetIntField(env, dcbObject, DCBc.fBinary);
	dcbStruct->fDsrSensitivity = (*env)->GetIntField(env, dcbObject, DCBc.fDsrSensitivity);
	dcbStruct->fDtrControl = (*env)->GetIntField(env, dcbObject, DCBc.fDtrControl);
	dcbStruct->fDummy2 = (*env)->GetIntField(env, dcbObject, DCBc.fDummy2);
	dcbStruct->fErrorChar = (*env)->GetIntField(env, dcbObject,	DCBc.fErrorChar);
	dcbStruct->fInX = (*env)->GetIntField(env, dcbObject, DCBc.fInX);
	dcbStruct->fNull = (*env)->GetIntField(env, dcbObject, DCBc.fNull);
	dcbStruct->fOutX = (*env)->GetIntField(env, dcbObject, DCBc.fOutX);
	dcbStruct->fOutxCtsFlow = (*env)->GetIntField(env, dcbObject, DCBc.fOutxCtsFlow);
	dcbStruct->fOutxDsrFlow = (*env)->GetIntField(env, dcbObject, DCBc.fOutxDsrFlow);
	dcbStruct->fParity = (*env)->GetIntField(env, dcbObject, DCBc.fParity);
	dcbStruct->fRtsControl = (*env)->GetIntField(env, dcbObject, DCBc.fRtsControl);
	dcbStruct->fTXContinueOnXoff = (*env)->GetIntField(env, dcbObject, DCBc.fTXContinueOnXoff);
	dcbStruct->wReserved = (*env)->GetShortField(env, dcbObject, DCBc.wReserved);
	dcbStruct->wReserved1 = (*env)->GetShortField(env, dcbObject, DCBc.wReserved1);

	return dcbStruct;
}

/*
 * Sets the fields of the given jobject to the value of the given DCB*.
 */
void setDCBFields(JNIEnv *env, jobject dcbObject, DCB *dcbStruct) {
	if (!DCBc.cached)
		cacheDCBFields(env, dcbObject);

	(*env)->SetIntField(env, dcbObject, DCBc.BaudRate, (jint) dcbStruct->BaudRate);
	(*env)->SetByteField(env, dcbObject, DCBc.ByteSize,	(jbyte) dcbStruct->ByteSize);
	(*env)->SetIntField(env, dcbObject, DCBc.DCBlength,	(jint) dcbStruct->DCBlength);
	(*env)->SetCharField(env, dcbObject, DCBc.EofChar, (jchar) dcbStruct->EofChar);
	(*env)->SetCharField(env, dcbObject, DCBc.ErrorChar, (jchar) dcbStruct->ErrorChar);
	(*env)->SetCharField(env, dcbObject, DCBc.EvtChar, (jchar) dcbStruct->EvtChar);
	(*env)->SetByteField(env, dcbObject, DCBc.Parity, (jbyte) dcbStruct->Parity);
	(*env)->SetByteField(env, dcbObject, DCBc.StopBits, (jbyte) dcbStruct->StopBits);
	(*env)->SetCharField(env, dcbObject, DCBc.XoffChar,	(jchar) dcbStruct->XoffChar);
	(*env)->SetShortField(env, dcbObject, DCBc.XoffLim,	(jshort) dcbStruct->XoffLim);
	(*env)->SetCharField(env, dcbObject, DCBc.XonChar, (jchar) dcbStruct->XonChar);
	(*env)->SetShortField(env, dcbObject, DCBc.XonLim, (jshort) dcbStruct->XonLim);
	(*env)->SetIntField(env, dcbObject, DCBc.fAbortOnError,	(jint) dcbStruct->fAbortOnError);
	(*env)->SetIntField(env, dcbObject, DCBc.fBinary, (jint) dcbStruct->fBinary);
	(*env)->SetIntField(env, dcbObject, DCBc.fDsrSensitivity, (jint) dcbStruct->fDsrSensitivity);
	(*env)->SetIntField(env, dcbObject, DCBc.fDtrControl, (jint) dcbStruct->fDtrControl);
	(*env)->SetIntField(env, dcbObject, DCBc.fDummy2, (jint) dcbStruct->fDummy2);
	(*env)->SetIntField(env, dcbObject, DCBc.fErrorChar, (jint) dcbStruct->fErrorChar);
	(*env)->SetIntField(env, dcbObject, DCBc.fInX, (jint) dcbStruct->fInX);
	(*env)->SetIntField(env, dcbObject, DCBc.fNull, (jint) dcbStruct->fNull);
	(*env)->SetIntField(env, dcbObject, DCBc.fOutX, (jint) dcbStruct->fOutX);
	(*env)->SetIntField(env, dcbObject, DCBc.fOutxCtsFlow, (jint) dcbStruct->fOutxCtsFlow);
	(*env)->SetIntField(env, dcbObject, DCBc.fOutxDsrFlow, (jint) dcbStruct->fOutxDsrFlow);
	(*env)->SetIntField(env, dcbObject, DCBc.fParity, (jint) dcbStruct->fParity);
	(*env)->SetIntField(env, dcbObject, DCBc.fRtsControl, (jint) dcbStruct->fRtsControl);
	(*env)->SetIntField(env, dcbObject, DCBc.fTXContinueOnXoff,	(jint) dcbStruct->fTXContinueOnXoff);
	(*env)->SetShortField(env, dcbObject, DCBc.wReserved, (jshort) dcbStruct->wReserved);
	(*env)->SetShortField(env, dcbObject, DCBc.wReserved1, (jshort) dcbStruct->wReserved1);
}

// ***********************************************************************
// **** OVERLAPPED: ******************************************************
// ***********************************************************************

/*
 * a struct to cache the OVERLAPPED fields
 */
typedef struct OVERLAPPED_FID_CACHE {
	int cached;
	jclass clazz;
	jfieldID cPointer, /* Internal, InternalHigh, Offset, OffsetHigh, Pointer, */
			hEvent;
} OVERLAPPED_FID_CACHE;
// cache for OVERLAPPED fields
OVERLAPPED_FID_CACHE OVERLAPPEDc;

/*
 * Caches the OVERLAPPED fields in the OVERLAPPED_FID_CACHE.
 */
void cacheOVERLAPPEDFields(JNIEnv *env, jobject overlappedObject) {
	if (OVERLAPPEDc.cached)
		return;

	OVERLAPPEDc.clazz = (*env)->GetObjectClass(env, overlappedObject);

	OVERLAPPEDc.cPointer = (*env)->GetFieldID(env, OVERLAPPEDc.clazz, "cPointer", "I");

//	OVERLAPPEDc.Internal = (*env)->GetFieldID(env, OVERLAPPEDc.clazz, "Internal", "J");
//	OVERLAPPEDc.InternalHigh = (*env)->GetFieldID(env, OVERLAPPEDc.clazz, "InternalHigh", "J");
//	OVERLAPPEDc.Offset = (*env)->GetFieldID(env, OVERLAPPEDc.clazz, "Offset", "I");
//	OVERLAPPEDc.OffsetHigh = (*env)->GetFieldID(env, OVERLAPPEDc.clazz, "OffsetHigh", "I");
//	OVERLAPPEDc.Pointer = (*env)->GetFieldID(env, OVERLAPPEDc.clazz, "Pointer", "I");
	OVERLAPPEDc.hEvent = (*env)->GetFieldID(env, OVERLAPPEDc.clazz, "hEvent", "I");

	OVERLAPPEDc.cached = TRUE;
}

/*
 * Retrieves the fields from the given jobject and returns it as OVERLAPPED*.
 */
OVERLAPPED *getOVERLAPPED(JNIEnv *env, jobject overlappedObject) {
	if (!OVERLAPPEDc.cached)
		cacheOVERLAPPEDFields(env, overlappedObject);

	OVERLAPPED *overlapped = (OVERLAPPED *) (*env)->GetIntField(env, overlappedObject, OVERLAPPEDc.cPointer);

//	overlapped->Internal = (ULONG_PTR) (*env)->GetLongField(env, overlappedObject, OVERLAPPEDc.Internal);
//	overlapped->InternalHigh = (ULONG_PTR) (*env)->GetLongField(env, overlappedObject, OVERLAPPEDc.InternalHigh);
//	overlapped->Offset = (*env)->GetIntField(env, overlappedObject, OVERLAPPEDc.Offset);
//	overlapped->OffsetHigh = (*env)->GetIntField(env, overlappedObject, OVERLAPPEDc.OffsetHigh);
//	overlapped->Pointer = (PVOID) (*env)->GetIntField(env, overlappedObject, OVERLAPPEDc.Pointer);
	overlapped->hEvent = (HANDLE) (*env)->GetIntField(env, overlappedObject, OVERLAPPEDc.hEvent);

	return overlapped;
}

///*
// * Sets the fields of the given jobject to the value of the given OVERLAPPED*.
// */
//void setOVERLAPPED(JNIEnv *env, jobject overlappedObject,
//		OVERLAPPED *overlappedStruct) {
//	if (!OVERLAPPEDc.cached)
//		cacheOVERLAPPEDFields(env, overlappedObject);
//
////	(*env)->SetLongField(env, overlappedObject, OVERLAPPEDc.Internal, (jlong) overlappedStruct->Internal);
////	(*env)->SetLongField(env, overlappedObject, OVERLAPPEDc.InternalHigh, (jlong) overlappedStruct->InternalHigh);
////	(*env)->SetIntField(env, overlappedObject, OVERLAPPEDc.Offset, (jint) overlappedStruct->Offset);
////	(*env)->SetIntField(env, overlappedObject, OVERLAPPEDc.OffsetHigh, (jint) overlappedStruct->OffsetHigh);
////	(*env)->SetIntField(env, overlappedObject, OVERLAPPEDc.Pointer, (jint) overlappedStruct->Pointer);
//	(*env)->SetIntField(env, overlappedObject, OVERLAPPEDc.hEvent, (jint) overlappedStruct->hEvent);
//}

// ****************************************************************
// **** INT: ******************************************************
// ****************************************************************

/*
 * a struct to cache the INT fields
 */
typedef struct INT_FID_CACHE {
	int cached;
	jclass clazz;
	jfieldID value;
} INT_FID_CACHE;
// cache for INT fields
INT_FID_CACHE INTc;

/*
 * Caches the INT fields in the INT_FID_CACHE.
 */
void cacheINT(JNIEnv *env, jobject intObject) {
	if (INTc.cached)
		return;

	INTc.clazz = (*env)->GetObjectClass(env, intObject);
	INTc.value = (*env)->GetFieldID(env, INTc.clazz, "value", "I");

	INTc.cached = TRUE;
}

/*
 * Retrieves the fields from the given jobject and returns it as DWORD*.
 */
DWORD *getINT(JNIEnv *env, jobject intObject, DWORD *intPointer) {
	if (!INTc.cached)
		cacheINT(env, intObject);

	*intPointer = (DWORD) (*env)->GetIntField(env, intObject, INTc.value);

	return intPointer;
}

/*
 * Sets the fields of the given jobject to the value of the given DWORD*.
 */
void setINT(JNIEnv *env, jobject intObject, DWORD *intPointer) {
	if (!INTc.cached)
		cacheINT(env, intObject);

	(*env)->SetIntField(env, intObject, INTc.value, (jint) *intPointer);
}

// ***********************************************************************
// **** HKEY: ************************************************************
// ***********************************************************************

/*
 * a struct to cache the HKEY fields
 */
typedef struct HKEY_FID_CACHE {
	int cached;
	jclass clazz;
	jfieldID cPointer;
} HKEY_FID_CACHE;
// cache for HKEY fields
HKEY_FID_CACHE HKEYc;

/*
 * Caches the HKEY fields in the HKEY_FID_CACHE.
 */
void cacheHKEYFields(JNIEnv *env, jobject hkeyObject) {
	if (HKEYc.cached)
		return;

	HKEYc.clazz = (*env)->GetObjectClass(env, hkeyObject);

	HKEYc.cPointer = (*env)->GetFieldID(env, HKEYc.clazz, "cPointer", "I");

	HKEYc.cached = TRUE;
}

/*
 * Retrieves the fields from the given jobject and returns it as HKEY*.
 */
HKEY *getHKEY(JNIEnv *env, jobject hkeyObject) {
	if (!HKEYc.cached)
		cacheHKEYFields(env, hkeyObject);

	HKEY *hkey = (HKEY *) (*env)->GetIntField(env, hkeyObject, HKEYc.cPointer);

	return hkey;
}

// ***********************************************************************
// **** NativeByteArray: *************************************************
// ***********************************************************************

/*
 * a struct to cache the NativeByteArray fields
 */
typedef struct NativeByteArray_FID_CACHE {
	int cached;
	jclass clazz;
	jfieldID cPointer;
} NativeByteArray_FID_CACHE;
// cache for NativeByteArray fields
NativeByteArray_FID_CACHE NativeByteArrayc;

/*
 * Caches the NativeByteArray fields in the NativeByteArray_FID_CACHE.
 */
void cacheNativeByteArrayFields(JNIEnv *env, jobject nativeByteArray) {
	if (NativeByteArrayc.cached)
		return;

	NativeByteArrayc.clazz = (*env)->GetObjectClass(env, nativeByteArray);

	NativeByteArrayc.cPointer = (*env)->GetFieldID(env, NativeByteArrayc.clazz, "cPointer", "I");

	NativeByteArrayc.cached = TRUE;
}

/*
 * Retrieves the fields from the given jobject and returns it as jbyte*.
 */
jbyte *getNativeByteArray(JNIEnv *env, jobject nativeByteArray) {
	if (!NativeByteArrayc.cached)
		cacheNativeByteArrayFields(env, nativeByteArray);

	jbyte *bytes = (jbyte *) (*env)->GetIntField(env, nativeByteArray, NativeByteArrayc.cPointer);

	return bytes;
}


