package org.translateToSql;

import org.translateToSql.twoVL.TwoVL;

import java.util.Objects;


public class Main {
    public static void main(String[] args) {
        TwoVL algo = new TwoVL();

        String sql = "SELECT R.A FROM R WHERE R.A != S.A OR NOT (R.A=1)";
        sql = algo.translate(sql);
        String result = "SELECT R.A FROM R WHERE (R.A IS NULL OR S.A IS NULL OR NOT R.A = S.A) OR (R.A IS NULL OR NOT R.A = 1)";
        System.out.println(Objects.equals(sql, result));
    }
}

