/*
 * OS_structs.h
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
#ifndef OS_STRUCTS_H_
#define OS_STRUCTS_H_

void cacheDCBFields(JNIEnv *, jobject);
DCB *getDCBFields(JNIEnv *, jobject, DCB *);
void setDCBFields(JNIEnv *, jobject, DCB *);

void cacheOVERLAPPEDFields(JNIEnv *, jobject);
OVERLAPPED *getOVERLAPPED(JNIEnv *, jobject);
void setOVERLAPPED(JNIEnv *, jobject, OVERLAPPED *);

void cacheINT(JNIEnv *env, jobject);
DWORD *getINT(JNIEnv *, jobject, DWORD *);
void setINT(JNIEnv *, jobject, DWORD *);

#endif /* OS_STRUCTS_H_ */
