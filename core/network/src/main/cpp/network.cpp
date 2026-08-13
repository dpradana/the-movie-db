#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_id_dpradana_themoviedb_core_network_config_NetworkConfig_getApiKey(
        JNIEnv* env,
        jobject /* this */) {
    std::string api_key = "be8b6c8aa9a5f4e240bb6093f9849051";
    return env->NewStringUTF(api_key.c_str());
}
