package org.example;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        System.out.println("Hello World!");

        int[][] directions = {  {-1,0},
                                {1,0},
                                {0,1},
                                {0,1}};
        System.out.println(directions[0][0]);
        System.out.println(directions[1][0]);
        System.out.println(directions[2][0]);
        System.out.println(directions[3][0]);

        System.out.println();
        System.out.println(directions[0][1]);
        System.out.println(directions[1][1]);
        System.out.println(directions[2][1]);
        System.out.println(directions[3][1]);
    }
}
