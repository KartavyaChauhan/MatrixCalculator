#include <jni.h>
#include "eigen-3.3.9/Eigen/Dense"
using namespace Eigen;

extern "C" JNIEXPORT jfloatArray JNICALL
Java_com_example_matrixcalculator_MainActivity_addMatrices(JNIEnv *env, jobject,
                                                           jfloatArray mat1,
                                                           jfloatArray mat2,
                                                           jint rows1, jint cols1,
                                                           jint rows2, jint cols2) {
    if (rows1 != rows2 || cols1 != cols2) {
        return env->NewFloatArray(0); // Return empty array on dimension mismatch
    }

    jfloat *matrix1 = env->GetFloatArrayElements(mat1, nullptr);
    jfloat *matrix2 = env->GetFloatArrayElements(mat2, nullptr);

    Map<MatrixXf> A(matrix1, rows1, cols1);
    Map<MatrixXf> B(matrix2, rows2, cols2);
    MatrixXf result = A + B;

    env->ReleaseFloatArrayElements(mat1, matrix1, 0);
    env->ReleaseFloatArrayElements(mat2, matrix2, 0);

    jfloatArray res = env->NewFloatArray(rows1 * cols1);
    env->SetFloatArrayRegion(res, 0, rows1 * cols1, result.data());
    return res;
}

extern "C" JNIEXPORT jfloatArray JNICALL
Java_com_example_matrixcalculator_MainActivity_subtractMatrices(JNIEnv *env, jobject,
                                                                jfloatArray mat1,
                                                                jfloatArray mat2,
                                                                jint rows1, jint cols1,
                                                                jint rows2, jint cols2) {
    if (rows1 != rows2 || cols1 != cols2) {
        return env->NewFloatArray(0);
    }

    jfloat *matrix1 = env->GetFloatArrayElements(mat1, nullptr);
    jfloat *matrix2 = env->GetFloatArrayElements(mat2, nullptr);

    Map<MatrixXf> A(matrix1, rows1, cols1);
    Map<MatrixXf> B(matrix2, rows2, cols2);
    MatrixXf result = A - B;

    env->ReleaseFloatArrayElements(mat1, matrix1, 0);
    env->ReleaseFloatArrayElements(mat2, matrix2, 0);

    jfloatArray res = env->NewFloatArray(rows1 * cols1);
    env->SetFloatArrayRegion(res, 0, rows1 * cols1, result.data());
    return res;
}

extern "C" JNIEXPORT jfloatArray JNICALL
Java_com_example_matrixcalculator_MainActivity_multiplyMatrices(JNIEnv *env, jobject,
                                                                jfloatArray mat1,
                                                                jfloatArray mat2,
                                                                jint rows1, jint cols1,
                                                                jint rows2, jint cols2) {
    if (cols1 != rows2) {
        return env->NewFloatArray(0);
    }

    jfloat *matrix1 = env->GetFloatArrayElements(mat1, nullptr);
    jfloat *matrix2 = env->GetFloatArrayElements(mat2, nullptr);

    Map<MatrixXf> A(matrix1, rows1, cols1);
    Map<MatrixXf> B(matrix2, rows2, cols2);
    MatrixXf result = A * B;

    env->ReleaseFloatArrayElements(mat1, matrix1, 0);
    env->ReleaseFloatArrayElements(mat2, matrix2, 0);

    jfloatArray res = env->NewFloatArray(rows1 * cols2);
    env->SetFloatArrayRegion(res, 0, rows1 * cols2, result.data());
    return res;
}

extern "C" JNIEXPORT jfloatArray JNICALL
Java_com_example_matrixcalculator_MainActivity_divideMatrices(JNIEnv *env, jobject,
                                                              jfloatArray mat1,
                                                              jfloatArray mat2,
                                                              jint rows1, jint cols1,
                                                              jint rows2, jint cols2) {
    if (cols1 != rows2 || rows2 != cols2) {
        return env->NewFloatArray(0);
    }

    jfloat *matrix1 = env->GetFloatArrayElements(mat1, nullptr);
    jfloat *matrix2 = env->GetFloatArrayElements(mat2, nullptr);

    Map<MatrixXf> A(matrix1, rows1, cols1);
    Map<MatrixXf> B(matrix2, rows2, cols2);
    MatrixXf B_inv = B.inverse();
    if (B_inv.hasNaN()) {
        return env->NewFloatArray(0); // Non-invertible matrix
    }
    MatrixXf result = A * B_inv;

    env->ReleaseFloatArrayElements(mat1, matrix1, 0);
    env->ReleaseFloatArrayElements(mat2, matrix2, 0);

    jfloatArray res = env->NewFloatArray(rows1 * cols2);
    env->SetFloatArrayRegion(res, 0, rows1 * cols2, result.data());
    return res;
}