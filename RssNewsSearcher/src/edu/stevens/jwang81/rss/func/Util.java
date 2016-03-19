package edu.stevens.jwang81.rss.func;



public class Util {

	public static boolean isStopWords(String word) {
		if (RssReader.stopWords.containsKey(word)) {
			return true;
		} else {
			return false;
		}
	}
	public static String upperToLower(String str)
    {
    	char[] ch = str.toCharArray();
    	for(int i=0;i<ch.length;i++)
    	{
    		if(((int)ch[i]>64) && ((int)ch[i]<91))
    		{
    			ch[i] = (char)((int)ch[i]+32);
    		}
    	}
    	String childStr = String.valueOf(ch);
    	return childStr;
    }
}
