/* DO NOT EDIT THIS FILE - it is machine generated */
#include "jni.h"
/* Header for class org_xidobi_OS */

#ifndef _Included_org_xidobi_OS
#define _Included_org_xidobi_OS
#ifdef __cplusplus
extern "C" {
#endif
#undef org_xidobi_OS_GENERIC_READ
#define org_xidobi_OS_GENERIC_READ -2147483648L
#undef org_xidobi_OS_GENERIC_WRITE
#define org_xidobi_OS_GENERIC_WRITE 1073741824L
#undef org_xidobi_OS_OPEN_EXISTING
#define org_xidobi_OS_OPEN_EXISTING 3L
#undef org_xidobi_OS_FILE_FLAG_OVERLAPPED
#define org_xidobi_OS_FILE_FLAG_OVERLAPPED 1073741824L
#undef org_xidobi_OS_INVALID_HANDLE_VALUE
#define org_xidobi_OS_INVALID_HANDLE_VALUE -1L
#undef org_xidobi_OS_ERROR_SUCCESS
#define org_xidobi_OS_ERROR_SUCCESS 0L
#undef org_xidobi_OS_ERROR_ACCESS_DENIED
#define org_xidobi_OS_ERROR_ACCESS_DENIED 5L
#undef org_xidobi_OS_ERROR_FILE_NOT_FOUND
#define org_xidobi_OS_ERROR_FILE_NOT_FOUND 2L
#undef org_xidobi_OS_ERROR_IO_PENDING
#define org_xidobi_OS_ERROR_IO_PENDING 997L
#undef org_xidobi_OS_ERROR_NO_MORE_ITEMS
#define org_xidobi_OS_ERROR_NO_MORE_ITEMS 259L
#undef org_xidobi_OS_WAIT_ABANDONED
#define org_xidobi_OS_WAIT_ABANDONED 128L
#undef org_xidobi_OS_WAIT_OBJECT_0
#define org_xidobi_OS_WAIT_OBJECT_0 0L
#undef org_xidobi_OS_WAIT_TIMEOUT
#define org_xidobi_OS_WAIT_TIMEOUT 258L
#undef org_xidobi_OS_WAIT_FAILED
#define org_xidobi_OS_WAIT_FAILED -1L
#undef org_xidobi_OS_KEY_WRITE
#define org_xidobi_OS_KEY_WRITE 131078L
#undef org_xidobi_OS_KEY_EXECUTE
#define org_xidobi_OS_KEY_EXECUTE 131097L
#undef org_xidobi_OS_KEY_READ
#define org_xidobi_OS_KEY_READ 131097L
#undef org_xidobi_OS_HKEY_LOCAL_MACHINE
#define org_xidobi_OS_HKEY_LOCAL_MACHINE -2147483646L
/*
 * Class:     org_xidobi_OS
 * Method:    CreateFile
 * Signature: (Ljava/lang/String;IIIIII)I
 */
JNIEXPORT jint JNICALL Java_org_xidobi_OS_CreateFile
  (JNIEnv *, jclass, jstring, jint, jint, jint, jint, jint, jint);

/*
 * Class:     org_xidobi_OS
 * Method:    CloseHandle
 * Signature: (I)Z
 */
JNIEXPORT jboolean JNICALL Java_org_xidobi_OS_CloseHandle
  (JNIEnv *, jclass, jint);

/*
 * Class:     org_xidobi_OS
 * Method:    GetCommState
 * Signature: (ILorg/xidobi/structs/DCB;)Z
 */
JNIEXPORT jboolean JNICALL Java_org_xidobi_OS_GetCommState
  (JNIEnv *, jclass, jint, jobject);

/*
 * Class:     org_xidobi_OS
 * Method:    SetCommState
 * Signature: (ILorg/xidobi/structs/DCB;)Z
 */
JNIEXPORT jboolean JNICALL Java_org_xidobi_OS_SetCommState
  (JNIEnv *, jclass, jint, jobject);

/*
 * Class:     org_xidobi_OS
 * Method:    CreateEventA
 * Signature: (IZZLjava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_org_xidobi_OS_CreateEventA
  (JNIEnv *, jclass, jint, jboolean, jboolean, jstring);

/*
 * Class:     org_xidobi_OS
 * Method:    WriteFile
 * Signature: (I[BILorg/xidobi/structs/INT;Lorg/xidobi/structs/OVERLAPPED;)Z
 */
JNIEXPORT jboolean JNICALL Java_org_xidobi_OS_WriteFile
  (JNIEnv *, jclass, jint, jbyteArray, jint, jobject, jobject);

/*
 * Class:     org_xidobi_OS
 * Method:    ReadFile
 * Signature: (I[BILorg/xidobi/structs/INT;Lorg/xidobi/structs/OVERLAPPED;)Z
 */
JNIEXPORT jboolean JNICALL Java_org_xidobi_OS_ReadFile
  (JNIEnv *, jclass, jint, jbyteArray, jint, jobject, jobject);

/*
 * Class:     org_xidobi_OS
 * Method:    GetLastError
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_org_xidobi_OS_GetLastError
  (JNIEnv *, jclass);

/*
 * Class:     org_xidobi_OS
 * Method:    GetOverlappedResult
 * Signature: (ILorg/xidobi/structs/OVERLAPPED;Lorg/xidobi/structs/INT;Z)Z
 */
JNIEXPORT jboolean JNICALL Java_org_xidobi_OS_GetOverlappedResult
  (JNIEnv *, jclass, jint, jobject, jobject, jboolean);

/*
 * Class:     org_xidobi_OS
 * Method:    WaitForSingleObject
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_org_xidobi_OS_WaitForSingleObject
  (JNIEnv *, jclass, jint, jint);

/*
 * Class:     org_xidobi_OS
 * Method:    RegOpenKeyExA
 * Signature: (ILjava/lang/String;IILorg/xidobi/structs/INT;)I
 */
JNIEXPORT jint JNICALL Java_org_xidobi_OS_RegOpenKeyExA
  (JNIEnv *, jclass, jint, jstring, jint, jint, jobject);

/*
 * Class:     org_xidobi_OS
 * Method:    RegCloseKey
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_org_xidobi_OS_RegCloseKey
  (JNIEnv *, jclass, jint);

/*
 * Class:     org_xidobi_OS
 * Method:    RegEnumValue
 * Signature: (IILjava/lang/String;III[BI)I
 */
JNIEXPORT jint JNICALL Java_org_xidobi_OS_RegEnumValue
  (JNIEnv *, jclass, jint, jint, jstring, jint, jint, jint, jbyteArray, jint);

/*
 * Class:     org_xidobi_OS
 * Method:    malloc
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_org_xidobi_OS_malloc
  (JNIEnv *, jclass, jint);

/*
 * Class:     org_xidobi_OS
 * Method:    free
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_org_xidobi_OS_free
  (JNIEnv *, jclass, jint);

/*
 * Class:     org_xidobi_OS
 * Method:    sizeOf_OVERLAPPED
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_org_xidobi_OS_sizeOf_1OVERLAPPED
  (JNIEnv *, jclass);

#ifdef __cplusplus
}
#endif
#endif
