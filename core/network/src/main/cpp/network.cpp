#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_id_dpradana_themoviedb_core_network_config_NetworkConfig_getApiKey(
        JNIEnv* env,
        jobject /* this */) {
    std::string api_key = "YOUR_API_KEY_HERE";
    return env->NewStringUTF(api_key.c_str());
}
