#ifndef ANDROID_HELPER
#define ANDROID_HELPER

#include <string>
#include <map>
#include <android/asset_manager.h>

//#include <cvrUtil/AndroidStdio.h>

#include <stack>
#include <iosfwd>
#include <GLES3/gl32.h>

#include <android/log.h>
#define TAG "GLES-TEMPLATE"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,TAG ,__VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,TAG ,__VA_ARGS__)

#define getenv(x) __android_getenv(x)
#define setenv(x,y) __android_setenv(x,y)

const char * __android_getenv(const char * name);

void __android_setenv(std::string key, std::string value);



class Environment {
private:
    static Environment* _ptr;
    std::map<std::string, std::string> _env;
    Environment();

public:
    static Environment * instance();
    const char * getVar(const char* name);
    void setVar(std::string key, std::string value);
};

class assetLoader{
private:
    static assetLoader* _myPtr;
    AAssetManager * const _asset_manager;

    bool GL_checkGlError(const char* funcName);
    GLuint GL_createShader(GLenum shaderType, const char *pSource);
//    GLuint GL_createProgram(const char* vtxSrc, const char* fragSrc);

    bool LoadTextFileFromAssetManager(const char* file_name, std::string* out_file_text_string);

public:
    static assetLoader * instance();
    assetLoader(AAssetManager * assetManager);
    GLuint GL_createProgram(const char* vtxSrc, const char* fragSrc);
    GLuint createGLShaderProgramFromFile(const char* vert_file, const char *_frag_file);

};



#endif
