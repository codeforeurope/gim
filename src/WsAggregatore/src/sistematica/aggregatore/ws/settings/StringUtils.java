/**
 * 
 */
package sistematica.aggregatore.ws.settings;

/**
 * @author arossini
 * 
 */
public class StringUtils
{
	private static final int PAD_LIMIT = 8192;

	/**
	 * Right pad a String with a specified character.
	 * 
	 * The String is padded to the size of <code>size</code>.
	 * 
	 * <pre>
	 * StringUtils.rightPad(null, *, *)     = null
	 * StringUtils.rightPad(&quot;&quot;, 3, 'z')     = &quot;zzz&quot;
	 * StringUtils.rightPad(&quot;bat&quot;, 3, 'z')  = &quot;bat&quot;
	 * StringUtils.rightPad(&quot;bat&quot;, 5, 'z')  = &quot;batzz&quot;
	 * StringUtils.rightPad(&quot;bat&quot;, 1, 'z')  = &quot;bat&quot;
	 * StringUtils.rightPad(&quot;bat&quot;, -1, 'z') = &quot;bat&quot;
	 * </pre>
	 * 
	 * @param str the String to pad out, may be null
	 * @param size the size to pad to
	 * @param padChar the character to pad with
	 * @return right padded String or original String if no padding is
	 *         necessary, <code>null</code> if null String input
	 * @since 2.0
	 */
	public static String rightPad(String str, int size, char padChar)
	{
		if (str == null)
		{
			return null;
		}
		int pads = size - str.length();
		if (pads <= 0)
		{
			return str; // returns original String when possible
		}
		if (pads > PAD_LIMIT)
		{
			return rightPad(str, size, String.valueOf(padChar));
		}
		return str.concat(padding(pads, padChar));
	}

	/**
	 * Right pad a String with a specified String.
	 * 
	 * The String is padded to the size of <code>size</code>.
	 * 
	 * <pre>
	 * StringUtils.rightPad(null, *, *)      = null
	 * StringUtils.rightPad(&quot;&quot;, 3, &quot;z&quot;)      = &quot;zzz&quot;
	 * StringUtils.rightPad(&quot;bat&quot;, 3, &quot;yz&quot;)  = &quot;bat&quot;
	 * StringUtils.rightPad(&quot;bat&quot;, 5, &quot;yz&quot;)  = &quot;batyz&quot;
	 * StringUtils.rightPad(&quot;bat&quot;, 8, &quot;yz&quot;)  = &quot;batyzyzy&quot;
	 * StringUtils.rightPad(&quot;bat&quot;, 1, &quot;yz&quot;)  = &quot;bat&quot;
	 * StringUtils.rightPad(&quot;bat&quot;, -1, &quot;yz&quot;) = &quot;bat&quot;
	 * StringUtils.rightPad(&quot;bat&quot;, 5, null)  = &quot;bat  &quot;
	 * StringUtils.rightPad(&quot;bat&quot;, 5, &quot;&quot;)    = &quot;bat  &quot;
	 * </pre>
	 * 
	 * @param str the String to pad out, may be null
	 * @param size the size to pad to
	 * @param padStr the String to pad with, null or empty treated as single
	 *            space
	 * @return right padded String or original String if no padding is
	 *         necessary, <code>null</code> if null String input
	 */
	public static String rightPad(String str, int size, String padStr)
	{
		if (str == null)
		{
			return null;
		}
		if (isEmpty(padStr))
		{
			padStr = " ";
		}
		int padLen = padStr.length();
		int strLen = str.length();
		int pads = size - strLen;
		if (pads <= 0)
		{
			return str; // returns original String when possible
		}
		if (padLen == 1 && pads <= PAD_LIMIT)
		{
			return rightPad(str, size, padStr.charAt(0));
		}

		if (pads == padLen)
		{
			return str.concat(padStr);
		}
		else if (pads < padLen)
		{
			return str.concat(padStr.substring(0, pads));
		}
		else
		{
			char[] padding = new char[pads];
			char[] padChars = padStr.toCharArray();
			for (int i = 0; i < pads; i++)
			{
				padding[i] = padChars[i % padLen];
			}
			return str.concat(new String(padding));
		}
	}

	/**
	 * Returns padding using the specified delimiter repeated to a given length.
	 * 
	 * <pre>
	 * StringUtils.padding(0, 'e')  = &quot;&quot;
	 * StringUtils.padding(3, 'e')  = &quot;eee&quot;
	 * StringUtils.padding(-2, 'e') = IndexOutOfBoundsException
	 * </pre>
	 * 
	 * Note: this method doesn't not support padding with <a
	 * href="http://www.unicode.org/glossary/#supplementary_character">Unicode
	 * Supplementary Characters</a> as they require a pair of <code>char</code>s
	 * to be represented. If you are needing to support full I18N of your
	 * applications consider using {@link #repeat(String, int)} instead.
	 * 
	 * 
	 * @param repeat number of times to repeat delim
	 * @param padChar character to repeat
	 * @return String with repeated character
	 * @throws IndexOutOfBoundsException if <code>repeat &lt; 0</code>
	 * @see #repeat(String, int)
	 */
	private static String padding(int repeat, char padChar) throws IndexOutOfBoundsException
	{
		if (repeat < 0)
		{
			throw new IndexOutOfBoundsException("Cannot pad a negative amount: " + repeat);
		}
		final char[] buf = new char[repeat];
		for (int i = 0; i < buf.length; i++)
		{
			buf[i] = padChar;
		}
		return new String(buf);
	}

	// Empty checks
	// -----------------------------------------------------------------------
	/**
	 * Checks if a String is empty ("") or null.
	 * 
	 * <pre>
	 * StringUtils.isEmpty(null)      = true
	 * StringUtils.isEmpty(&quot;&quot;)        = true
	 * StringUtils.isEmpty(&quot; &quot;)       = false
	 * StringUtils.isEmpty(&quot;bob&quot;)     = false
	 * StringUtils.isEmpty(&quot;  bob  &quot;) = false
	 * </pre>
	 * 
	 * NOTE: This method changed in Lang version 2.0. It no longer trims the
	 * String. That functionality is available in isBlank().
	 * 
	 * @param str the String to check, may be null
	 * @return <code>true</code> if the String is empty or null
	 */
	public static boolean isEmpty(String str)
	{
		return str == null || str.length() == 0;
	}

	/**
	 * Left pad a String with a specified character.
	 * 
	 * Pad to a size of <code>size</code>.
	 * 
	 * <pre>
	 * StringUtils.leftPad(null, *, *)     = null
	 * StringUtils.leftPad(&quot;&quot;, 3, 'z')     = &quot;zzz&quot;
	 * StringUtils.leftPad(&quot;bat&quot;, 3, 'z')  = &quot;bat&quot;
	 * StringUtils.leftPad(&quot;bat&quot;, 5, 'z')  = &quot;zzbat&quot;
	 * StringUtils.leftPad(&quot;bat&quot;, 1, 'z')  = &quot;bat&quot;
	 * StringUtils.leftPad(&quot;bat&quot;, -1, 'z') = &quot;bat&quot;
	 * </pre>
	 * 
	 * @param str the String to pad out, may be null
	 * @param size the size to pad to
	 * @param padChar the character to pad with
	 * @return left padded String or original String if no padding is necessary,
	 *         <code>null</code> if null String input
	 * @since 2.0
	 */
	public static String leftPad(String str, int size, char padChar)
	{
		if (str == null)
		{
			return null;
		}
		int pads = size - str.length();
		if (pads <= 0)
		{
			return str; // returns original String when possible
		}
		if (pads > PAD_LIMIT)
		{
			return leftPad(str, size, String.valueOf(padChar));
		}
		return padding(pads, padChar).concat(str);
	}

	/**
	 * Left pad a String with a specified String.
	 * 
	 * Pad to a size of <code>size</code>.
	 * 
	 * <pre>
	 * StringUtils.leftPad(null, *, *)      = null
	 * StringUtils.leftPad(&quot;&quot;, 3, &quot;z&quot;)      = &quot;zzz&quot;
	 * StringUtils.leftPad(&quot;bat&quot;, 3, &quot;yz&quot;)  = &quot;bat&quot;
	 * StringUtils.leftPad(&quot;bat&quot;, 5, &quot;yz&quot;)  = &quot;yzbat&quot;
	 * StringUtils.leftPad(&quot;bat&quot;, 8, &quot;yz&quot;)  = &quot;yzyzybat&quot;
	 * StringUtils.leftPad(&quot;bat&quot;, 1, &quot;yz&quot;)  = &quot;bat&quot;
	 * StringUtils.leftPad(&quot;bat&quot;, -1, &quot;yz&quot;) = &quot;bat&quot;
	 * StringUtils.leftPad(&quot;bat&quot;, 5, null)  = &quot;  bat&quot;
	 * StringUtils.leftPad(&quot;bat&quot;, 5, &quot;&quot;)    = &quot;  bat&quot;
	 * </pre>
	 * 
	 * @param str the String to pad out, may be null
	 * @param size the size to pad to
	 * @param padStr the String to pad with, null or empty treated as single
	 *            space
	 * @return left padded String or original String if no padding is necessary,
	 *         <code>null</code> if null String input
	 */
	public static String leftPad(String str, int size, String padStr)
	{
		if (str == null)
		{
			return null;
		}
		if (isEmpty(padStr))
		{
			padStr = " ";
		}
		int padLen = padStr.length();
		int strLen = str.length();
		int pads = size - strLen;
		if (pads <= 0)
		{
			return str; // returns original String when possible
		}
		if (padLen == 1 && pads <= PAD_LIMIT)
		{
			return leftPad(str, size, padStr.charAt(0));
		}

		if (pads == padLen)
		{
			return padStr.concat(str);
		}
		else if (pads < padLen)
		{
			return padStr.substring(0, pads).concat(str);
		}
		else
		{
			char[] padding = new char[pads];
			char[] padChars = padStr.toCharArray();
			for (int i = 0; i < pads; i++)
			{
				padding[i] = padChars[i % padLen];
			}
			return new String(padding).concat(str);
		}
	}

}
