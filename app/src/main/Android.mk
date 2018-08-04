# modify by <zhonggao-smartwireless-2010-10-11>. #
LOCAL_PATH:= $(call my-dir)

# module for CIT

include $(CLEAR_VARS)

LOCAL_SRC_FILES := $(call all-subdir-java-files)

LOCAL_MODULE_TAGS := optional eng
LOCAL_PROGUARD_ENABLED := disabled
LOCAL_PRIVILEGED_MODULE := true

LOCAL_PACKAGE_NAME := CIT
LOCAL_CERTIFICATE := platform

LOCAL_JNI_SHARED_LIBRARIES := libqcomfm_jni

LOCAL_JNI_SHARED_LIBRARIES += libCommonDrive
LOCAL_JAVA_LIBRARIES := readboynv qcom.fmradio org.apache.http.legacy

include $(BUILD_PACKAGE)

include $(call all-makefiles-under,$(LOCAL_PATH))

