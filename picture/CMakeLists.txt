cmake_minimum_required(VERSION 3.4.1)


#指定要引用的libjpeg.so的头文件目录
INCLUDE_DIRECTORIES(
        src/main/cpp
)

set(libjpeg_lib_dir ${CMAKE_SOURCE_DIR}/libs)
set(INC_DIR ${CMAKE_SOURCE_DIR}/libs/libjpeg-turbo)
#添加头文件目录
include_directories(${INC_DIR})
add_library(libjpeg SHARED IMPORTED)
set_target_properties(libjpeg PROPERTIES IMPORTED_LOCATION ${libjpeg_lib_dir}/${ANDROID_ABI}/libjpeg.so)

add_library( # Sets the name of the library.
        gauss

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        src/main/cpp/gauss.cpp)
add_library( # Sets the name of the library.
        picture-edit

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        src/main/cpp/picture-edit.cpp)
add_library( # Sets the name of the library.
        compress

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        src/main/cpp/compress.cpp)




find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)
#自己写的库，若与其他库连接，请分开书写，如下，picture-edit和compress分别与其他库连接，如果写在一起，则不起作用
target_link_libraries(picture-edit
                        gauss
                        libjpeg
                        jnigraphics
                        ${log-lib}
        )
target_link_libraries(
        compress
        libjpeg
        jnigraphics
        ${log-lib}
        )
