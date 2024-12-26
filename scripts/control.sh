set -u -e

# cspell:disable

SRC_DIR="src/main/*.java"
TEST_DIR="src/test/*.java"
BUILD_DIR_CLASS="build/class"
BUILD_DIR_TEST="build/test"
LIB_DIR="lib"
SOURCE=21
TARGET=21
VERBOSE=false


compile_source() {
    cmd="javac -source \"${SOURCE}\" -target \"${TARGET}\" -d \"${BUILD_DIR_CLASS}\" \"${SRC_DIR}\""
    if "$VERBOSE"; then
        cmd="${cmd} -verbose"
    fi
    echo $cmd
}

compile_test() {
    cmd="javac -source \"${SOURCE}\" -target \"${TARGET}\" -cp \"${LIB_DIR}/junit-platform-console-standalone-1.10.0.jar\" -d \"${BUILD_DIR_TEST}\" \"${TEST_DIR}\""
    if "$VERBOSE"; then
        cmd="${cmd} -verbose"
    fi
    echo $cmd
}

run_tests() {
    cmd="java -jar \"${LIB_DIR}/junit-platform-console-standalone-1.10.0.jar\" --class-path \"${BUILD_DIR_TEST}\" --scan-class-path"
    if "$VERBOSE"; then
        cmd="${cmd} -verbose"
    fi
    echo $cmd
}

read