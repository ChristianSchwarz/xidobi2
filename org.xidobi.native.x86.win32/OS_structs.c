/*
 * OS_structs.c
 *
 *   Created on: 03.01.2013
 *      Authors: Christian Schwarz, Tobias Breﬂler
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


typedef struct DCB_FID_CACHE {
	int cached;
	jclass clazz;
	jfieldID 	BaudRate,
				ByteSize,
				DCBlength,
				EofChar,
				ErrorChar,
				EvtChar,
				Parity,
				StopBits,
				XoffChar,
				XoffLim,
				XonChar,
				XonLim,
				fAbortOnError,
				fBinary,
				fDsrSensitivity,
				fDtrControl,
				fDummy2,
				fErrorChar,
				fInX,
				fNull,
				fOutX,
				fOutxCtsFlow,
				fOutxDsrFlow,
				fParity,
				fRtsControl,
				fTXContinueOnXoff,
				wReserved,
				wReserved1;
} DCB_FID_CACHE;
DCB_FID_CACHE DCBc;

void cacheDCBFields(JNIEnv *env, jobject lpObject) {
	if (DCBc.cached)
		return;

	DCBc.clazz = (*env)->GetObjectClass(env, lpObject);
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

void setDCBFields(JNIEnv *env, jobject dcbObject, DCB *dcbStruct) {
	if (!DCBc.cached)
		cacheDCBFields(env, dcbObject);

	(*env)->SetIntField(env, dcbObject, DCBc.BaudRate, (jint) dcbStruct->BaudRate);
	(*env)->SetByteField(env, dcbObject, DCBc.ByteSize, (jbyte) dcbStruct->ByteSize);
	(*env)->SetIntField(env, dcbObject, DCBc.DCBlength, (jint) dcbStruct->DCBlength);
	(*env)->SetCharField(env, dcbObject, DCBc.EofChar, (jchar) dcbStruct->EofChar);
	(*env)->SetCharField(env, dcbObject, DCBc.ErrorChar, (jchar) dcbStruct->ErrorChar);
	(*env)->SetCharField(env, dcbObject, DCBc.EvtChar, (jchar) dcbStruct->EvtChar);
	(*env)->SetByteField(env, dcbObject, DCBc.Parity, (jbyte) dcbStruct->Parity);
	(*env)->SetByteField(env, dcbObject, DCBc.StopBits, (jbyte) dcbStruct->StopBits);
	(*env)->SetCharField(env, dcbObject, DCBc.XoffChar, (jchar) dcbStruct->XoffChar);
	(*env)->SetShortField(env, dcbObject, DCBc.XoffLim, (jshort) dcbStruct->XoffLim);
	(*env)->SetCharField(env, dcbObject, DCBc.XonChar, (jchar) dcbStruct->XonChar);
	(*env)->SetShortField(env, dcbObject, DCBc.XonLim, (jshort) dcbStruct->XonLim);
	(*env)->SetIntField(env, dcbObject, DCBc.fAbortOnError, (jint) dcbStruct->fAbortOnError);
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
	(*env)->SetIntField(env, dcbObject, DCBc.fTXContinueOnXoff, (jint) dcbStruct->fTXContinueOnXoff);
	(*env)->SetShortField(env, dcbObject, DCBc.wReserved, (jshort) dcbStruct->wReserved);
	(*env)->SetShortField(env, dcbObject, DCBc.wReserved1, (jshort) dcbStruct->wReserved1);
}
