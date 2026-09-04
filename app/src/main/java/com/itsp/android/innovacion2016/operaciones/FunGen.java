package com.itsp.android.innovacion2016.operaciones;


import java.util.Date;

public class FunGen {

    public static double ConvertDoubleStr(String cad)
    {
        cad = cad.trim();
        if (cad.isEmpty())
        {
            return 0.0;
        }
        else
        {
            try
            {
                return Double.parseDouble(cad);
            }
            catch (Exception e)
            {
                return 0.0;
            }
        }
    }

    public static int ConvertIntStr(String cad)
    {
        cad = cad.trim();
        if (cad.equals(null) || cad.trim().isEmpty())
        {
            return 0;
        }
        else
        {
            try
            {
                return Integer.parseInt(cad);
            }
            catch (Exception e)
            {
                return 0;
            }
        }
    }

    public static float ConvertFloatStr(String cad)
    {
        cad = cad.trim();
        if (cad.trim().isEmpty())
        {
            return 0.0f;
        }
        else
        {
            try
            {
                return Float.parseFloat(cad);
            }
            catch (Exception e)
            {
                return 0.0f;
            }
        }
    }

    public static int CadeToInt(String cad)
    {
        cad = cad.trim();
        if (cad.equals(null) || cad.trim().isEmpty())
        {
            return 0;
        }
        else
        {
            try
            {
                return Integer.parseInt(cad);
            }
            catch (Exception e)
            {
                return 0;
            }
        }
    }

}