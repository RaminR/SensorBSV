package com.resud;

/**
 * @author Ramin
 * @version 1.0
 * @create 04.02.2017
 * @deprecated Имитационное моделирование
 */
public class BSV {
    private static int N = 10000;
    private static int M = 123456;
    private static int K = 7000;
    private static int intK = 10;
    private static double A[] = new double[N];
    private static double Z[] = new double[N];
    private static double P[] = new double[K];
    private static double n[] = new double[K];
    private static double imperP[] = new double[K];
    private static double SQDev[] = new double[K];
    private static double ABDev[] = new double[K];
    private static double SumSQDev, SumABDev, MaxDev;

    public static void main(String[] args) {
        System.out.println("----------------------\n" + "----------------------\n" + "----------------------\n" +
                "----- Датчик БСВ -----");
        A[0] = 24.0;
        System.out.print("N:\t " + N + ";\n" + "M:\t " + M + ";\n" + "K:\t " + K + ";\n" + "A[0]:\t" + A[0] + ";\nk:\t " + intK + "\n");
        SearchZ();
        CompareTo();
        Interval();
        MathExpAndDispersion();

    }

    private static void SearchZ() {
        System.out.println("----- 1. Выполнение формулы мультипликативного конгруэнтного датчика БСВ -----");
        for (int i = 0; i < N; i++) {
            if (i > 0) {
                A[i] = (A[i - 1] * K) % M;
                Z[i] = A[i] / M;
            }
            if (i < 20) {
                System.out.printf("A[%d]: %16.1f; \t\t|\t\tZ[%d]:\t\t %.4f;\n", (i + 1), A[i], (i + 1), Z[i]);
            }
        }

//        System.out.printf("A[%d]: %16.1f; \t\t|\t\tZ[%d]:\t\t %.4f;\n", (10000), A[9999], (10000), Z[9999]);
        System.out.println();
    }

    private static void CompareTo() {
        System.out.println("----- 2. Оценка длины периода датчика -----");
        boolean count = false;
        int pick = 0;
        int lel = 0;
        for (int i = 1; i < N; i++) {
            if (A[0] == A[i]) {
                pick = i;
                count = true;
                System.out.printf("A[%.4f] == A[%.4f]", A[0], A[i]);
            } else if (A[i] == A[N - 1] && i != N - 1) {
                pick = i;
                lel += i;
                count = true;
                System.out.printf("%.4f == A[%d] && %d != %d)\n", A[i], (N - 1), i, (N - 1));
            }
        }
        if (!count) {
            System.out.println("Последовательность: непериодическая;");
        } else {
            System.out.println("Последовательность: периодическая;" + " LEL :" + lel);
//            System.out.println("Первый элемент: " + A[0] + " : "
//                    + count + ";"
//                    + "\nПоследний элемент: " + A[N - 1] + ";"
//                    + "\nНайденное совпадение: место в массиве: "
//                    + (pick + 1)
//                    + " -- его значение: " + A[pick] + ";\n");
        }
    }

    private static void Interval() {
        System.out.println("----- 3. Интервал и эмпирические вероятности (P), Квадрат отклонения (σ), Абсолютное отклоенение (S) -----");

        for (double i = 0; i < intK; i++) {
            n[(int) i] = CountInterval(i / intK, (i + 1) / intK, N, Z);
            imperP[(int) i] = n[(int) i] / N;

            SQDev[(int) i] = SquareDeviation(imperP[(int) i]);
            SumSQDev += SQDev[(int) i];

            ABDev[(int) i] = AbsolytDeviation(imperP[(int) i]);
            SumABDev += ABDev[(int) i];
            //MaxDev = 0;
            System.out.println(String.format("%4.0f", i + 1) + ": [" + String.format("%.4f", i / intK) + ", "
                    + String.format("%.4f", (i + 1) / intK) + "];  "
                    + "Σ: ["
                    + String.format("%.4f", n[(int) i])
                    + "]; \tP: [" + String.format("%.4f", imperP[(int) i]) + "]; \tσ: ["
                    + String.format("%.4f", SquareDeviation(imperP[(int) i])) + "];"
                    + "\tS: [" + String.format("%.4f", AbsolytDeviation(imperP[(int) i])) + "];");
        }
        System.out.println("--------------------------------------------------------\n"
                + "Сумма квадаратов отклонения(σ): " + String.format("%.4f", SumSQDev)
                + "\nСумма абсолютных отклонений(S): " + String.format("%.4f", SumABDev)
                + "\nМаксимальное отклонение(G): " + String.format("%.4f", MaxDeviation()));
    }

    private static int CountInterval(double a, double b, int N, double z[]) {
        int c = 0;
        for (int i = 0; i < N; i++) {
            if (z[i] >= a && z[i] <= b) {
                c++;
            }
        }
        return c;
    }

    private static double SquareDeviation(double iP) {
        double dev = Math.pow((iP - 1.0 / intK), 2);
        return dev;
    }

    private static double AbsolytDeviation(double iP) {
        double dev = Math.abs(iP - (1.0 / intK));
        return dev;
    }

    private static double MaxDeviation() {
        MaxDev = ABDev[0];
        for (int i = 0; i < ABDev.length; i++) {
            if (ABDev[i] > MaxDev) {
                MaxDev = ABDev[i];
            }
        }
        return MaxDev;
    }

    private static void MathExpAndDispersion() {
        double M, D, MsumZ = 0, DsumZ = 0;
        for (int i = 0; i < N; i++) {
            MsumZ += Z[i];
            DsumZ += Math.pow(Z[i], 2);
        }
        M = (double) 1 / N * MsumZ;
        D = (double) 1 / N * DsumZ - Math.pow(M, 2);
        System.out.println("Математическое ожидаение: " + String.format("%.4f", M)
                + "\nДисперсия: " + String.format("%.4f", D));

        System.out.println("--------------------------------------------------------\n"
                + "M: " + String.format("%.4f", MsumZ / N)
                + "\nD: " + String.format("%.4f", DsumZ / N - Math.pow(MsumZ / N, 2)));
    }
}
