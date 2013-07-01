package gpsdatareceiver;

public class BlankRemover
{
    /* remove leading whitespace */
    public static String ltrim(String source)
    {
        String regex = "^\\s+";
        System.out.println(regex);
        return source.replaceAll(regex, "");
    }

    /* remove trailing whitespace */
    public static String rtrim(String source)
    {
        String regex = "\\s+$";
        System.out.println(regex);
        return source.replaceAll(regex, "");
    }

    /* replace multiple whitespaces between words with single blank */
    public static String itrim(String source)
    {
        String regex = "\\b\\s{2,}\\b";
        System.out.println(regex);
        return source.replaceAll(regex, " ");
    }

    public static String myInternTrim(String source)
    {
        String regex = "\\b\\s+\\b";
        System.out.println(regex);
        return source.replaceAll(regex, "");
    }

    /* remove all superfluous whitespaces in source string */
    public static String trim(String source)
    {
        return itrim(ltrim(rtrim(source)));
    }

    /* remove all whitespaces in source string */
    public static String myCompleteTrim(String source)
    {
        return myInternTrim(ltrim(rtrim(source)));
    }

    public static String lrtrim(String source)
    {
        return ltrim(rtrim(source));
    }

    public static void main(String[] args)
    {
        String oldStr = ">     <1-2-1-2-1-2-1-2-1-2-1-----2-1-2-1-2-1-2-1-2-1-2-1-2>   <";
        String newStr = oldStr.replaceAll("-", " ");
        System.out.println(newStr);
        System.out.println(ltrim(newStr));
        System.out.println(rtrim(newStr));
        System.out.println(itrim(newStr));
        System.out.println(lrtrim(newStr));
    }
}
