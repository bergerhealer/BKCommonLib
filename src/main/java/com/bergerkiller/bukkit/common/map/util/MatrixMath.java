package com.bergerkiller.bukkit.common.map.util;

public class MatrixMath {
    /**
     * Given a 4x4 array "matrix0", this function replaces it with the
     * LU decomposition of a row-wise permutation of itself.  The input
     * parameters are "matrix0" and "dimen".  The array "matrix0" is also
     * an output parameter.  The vector "row_perm[4]" is an output
     * parameter that contains the row permutations resulting from partial
     * pivoting.  The output parameter "even_row_xchg" is 1 when the
     * number of row exchanges is even, or -1 otherwise.  Assumes data
     * type is always double.
     *
     * This function is similar to luDecomposition, except that it
     * is tuned specifically for 4x4 matrices.
     *
     * @return true if the matrix is nonsingular, or false otherwise.
     */
    //
    // Reference: Press, Flannery, Teukolsky, Vetterling,
    //        _Numerical_Recipes_in_C_, Cambridge University Press,
    //        1988, pp 40-45.
    //
    public static boolean luDecomposition(double[] matrix0,
            int[] row_perm) {

        double row_scale[] = new double[4];

        // Determine implicit scaling information by looping over rows
        {
            int i, j;
            int ptr, rs;
            double big, temp;

            ptr = 0;
            rs = 0;

            // For each row ...
            i = 4;
            while (i-- != 0) {
                big = 0.0;

                // For each column, find the largest element in the row
                j = 4;
                while (j-- != 0) {
                    temp = matrix0[ptr++];
                    temp = Math.abs(temp);
                    if (temp > big) {
                        big = temp;
                    }
                }

                // Is the matrix singular?
                if (big == 0.0) {
                    return false;
                }
                row_scale[rs++] = 1.0 / big;
            }
        }

        {
            int j;
            int mtx;

            mtx = 0;

            // For all columns, execute Crout's method
            for (j = 0; j < 4; j++) {
                int i, imax, k;
                int target, p1, p2;
                double sum, big, temp;

                // Determine elements of upper diagonal matrix U
                for (i = 0; i < j; i++) {
                    target = mtx + (4*i) + j;
                    sum = matrix0[target];
                    k = i;
                    p1 = mtx + (4*i);
                    p2 = mtx + j;
                    while (k-- != 0) {
                        sum -= matrix0[p1] * matrix0[p2];
                        p1++;
                        p2 += 4;
                    }
                    matrix0[target] = sum;
                }

                // Search for largest pivot element and calculate
                // intermediate elements of lower diagonal matrix L.
                big = 0.0;
                imax = -1;
                for (i = j; i < 4; i++) {
                    target = mtx + (4*i) + j;
                    sum = matrix0[target];
                    k = j;
                    p1 = mtx + (4*i);
                    p2 = mtx + j;
                    while (k-- != 0) {
                        sum -= matrix0[p1] * matrix0[p2];
                        p1++;
                        p2 += 4;
                    }
                    matrix0[target] = sum;

                    // Is this the best pivot so far?
                    if ((temp = row_scale[i] * Math.abs(sum)) >= big) {
                        big = temp;
                        imax = i;
                    }
                }

                if (imax < 0) {
                    return false;
                }

                // Is a row exchange necessary?
                if (j != imax) {
                    // Yes: exchange rows
                    k = 4;
                    p1 = mtx + (4*imax);
                    p2 = mtx + (4*j);
                    while (k-- != 0) {
                        temp = matrix0[p1];
                        matrix0[p1++] = matrix0[p2];
                        matrix0[p2++] = temp;
                    }

                    // Record change in scale factor
                    row_scale[imax] = row_scale[j];
                }

                // Record row permutation
                row_perm[j] = imax;

                // Is the matrix singular
                if (matrix0[(mtx + (4*j) + j)] == 0.0) {
                    return false;
                }

                // Divide elements of lower diagonal matrix L by pivot
                if (j != (4-1)) {
                    temp = 1.0 / (matrix0[(mtx + (4*j) + j)]);
                    target = mtx + (4*(j+1)) + j;
                    i = 3 - j;
                    while (i-- != 0) {
                        matrix0[target] *= temp;
                        target += 4;
                    }
                }
            }
        }

        return true;
    }

    /**
     * Solves a set of linear equations.  The input parameters "matrix1",
     * and "row_perm" come from luDecompostionD4x4 and do not change
     * here.  The parameter "matrix2" is a set of column vectors assembled
     * into a 4x4 matrix of floating-point values.  The procedure takes each
     * column of "matrix2" in turn and treats it as the right-hand side of the
     * matrix equation Ax = LUx = b.  The solution vector replaces the
     * original column of the matrix.
     *
     * If "matrix2" is the identity matrix, the procedure replaces its contents
     * with the inverse of the matrix from which "matrix1" was originally
     * derived.
     */
    //
    // Reference: Press, Flannery, Teukolsky, Vetterling,
    //        _Numerical_Recipes_in_C_, Cambridge University Press,
    //        1988, pp 44-45.
    //
    public static void luBacksubstitution(double[] matrix1,
            int[] row_perm,
            double[] matrix2) {

        int i, ii, ip, j, k;
        int rp;
        int cv, rv;

        //  rp = row_perm;
        rp = 0;

        // For each column vector of matrix2 ...
        for (k = 0; k < 4; k++) {
            //      cv = &(matrix2[0][k]);
            cv = k;
            ii = -1;

            // Forward substitution
            for (i = 0; i < 4; i++) {
                double sum;

                ip = row_perm[rp+i];
                sum = matrix2[cv+4*ip];
                matrix2[cv+4*ip] = matrix2[cv+4*i];
                if (ii >= 0) {
                    //          rv = &(matrix1[i][0]);
                    rv = i*4;
                    for (j = ii; j <= i-1; j++) {
                        sum -= matrix1[rv+j] * matrix2[cv+4*j];
                    }
                }
                else if (sum != 0.0) {
                    ii = i;
                }
                matrix2[cv+4*i] = sum;
            }

            // Backsubstitution
            //      rv = &(matrix1[3][0]);
            rv = 3*4;
            matrix2[cv+4*3] /= matrix1[rv+3];

            rv -= 4;
            matrix2[cv+4*2] = (matrix2[cv+4*2] -
                    matrix1[rv+3] * matrix2[cv+4*3]) / matrix1[rv+2];

            rv -= 4;
            matrix2[cv+4*1] = (matrix2[cv+4*1] -
                    matrix1[rv+2] * matrix2[cv+4*2] -
                    matrix1[rv+3] * matrix2[cv+4*3]) / matrix1[rv+1];

            rv -= 4;
            matrix2[cv+4*0] = (matrix2[cv+4*0] -
                    matrix1[rv+1] * matrix2[cv+4*1] -
                    matrix1[rv+2] * matrix2[cv+4*2] -
                    matrix1[rv+3] * matrix2[cv+4*3]) / matrix1[rv+0];
        }
    }
}
