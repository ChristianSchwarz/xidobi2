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
	jfieldID BaudRate;
} DCB_FID_CACHE;

DCB_FID_CACHE DCBc;

void cacheDCBFields(JNIEnv *env, jobject lpObject) {
	if (DCBc.cached)
		return;

	DCBc.clazz = (*env)->GetObjectClass(env, lpObject);
	DCBc.BaudRate = (*env)->GetFieldID(env, DCBc.clazz, "BaudRate", "I");
	DCBc.cached = TRUE;
}

void setDCBFields(JNIEnv *env, jobject dcbObject, DCB *dcbStruct) {
	if (!DCBc.cached)
		cacheDCBFields(env, dcbObject);

	(*env)->SetIntField(env, dcbObject, DCBc.BaudRate, (jint) dcbStruct->BaudRate);
}
