//package com.doopp.iogame.util;
//
//import java.io.*;
//import java.util.*;
//
//public class SensitiveWord {
//
//    // 字符编码
//    private final String ENCODING = "UTF-8";
//
//    private final String replaceWord = "!@#$%^&*!@#$%^&*";
//
//    private final Map<String, String> sensitiveWordMap = new HashMap<>();
//
//    private final Map sensitiveWordDfaMap = new HashMap();
//
//    // 最小匹配规则
//    public static int minMatchType = 1;
//
//    // 最大匹配规则
//    public static int maxMatchType = 2;
//
//    private SensitiveWord() {
//    }
//
//    public SensitiveWord(String resourcePath) {
//        try {
//            this.sensitiveWordMapInit(resourcePath);
//        }
//        catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    // 判断文字是否包含敏感字符
//    public boolean isContaintSensitiveWord(String txt, int matchType) {
//        boolean flag = false;
//        for (int i = 0; i < txt.length(); i++) {
//            // 判断是否包含敏感字符
//            int matchFlag = this.checkSensitiveWord(txt, i, matchType);
//            // 大于0存在，返回true
//            if (matchFlag > 0) {
//                flag = true;
//            }
//        }
//        return flag;
//    }
//
//    // 获取文字中的敏感词
//    public Set<String> getSensitiveWord(String txt, int matchType) {
//        Set<String> sensitiveWordList = new HashSet<String>();
//        for (int i = 0; i < txt.length(); i++) {
//            // 判断是否包含敏感字符
//            int length = checkSensitiveWord(txt, i, matchType);
//            // 存在,加入list中
//            if (length > 0) {
//                sensitiveWordList.add(txt.substring(i, i + length));
//                // 减1的原因，是因为for会自增
//                i = i + length - 1;
//            }
//        }
//        return sensitiveWordList;
//    }
//
//    // 替换敏感字字符
//    public String replaceSensitiveWord(String txt, int matchType, String replaceChar) {
//        String resultTxt = txt;
//        // 获取所有的敏感词
//        Set<String> set = getSensitiveWord(txt, matchType);
//        Iterator<String> iterator = set.iterator();
//        String word = null;
//        String replaceString = null;
//        while (iterator.hasNext()) {
//            word = iterator.next();
//            replaceString = getReplaceChars(replaceChar, word.length());
//            resultTxt = resultTxt.replaceAll(word, replaceString);
//        }
//        return resultTxt;
//    }
//
//    // 获取替换字符串
//    private String getReplaceChars(String replaceChar, int length) {
//        String resultReplace = replaceChar;
//        for (int i = 1; i < length; i++) {
//            resultReplace += replaceChar;
//        }
//        return resultReplace;
//    }
//
//    // 检查文字中是否包含敏感字符，检查规则如下：<br>
//    // 如果存在，则返回敏感词字符的长度，不存在返回0
//    public int checkSensitiveWord(String txt, int beginIndex, int matchType) {
//        // 敏感词结束标识位：用于敏感词只有1位的情况
//        boolean flag = false;
//        // 匹配标识数默认为 0
//        int matchFlag = 0;
//        Map nowMap = sensitiveWordDfaMap;
//        for (int i = beginIndex; i < txt.length(); i++) {
//            char word = txt.charAt(i);
//            // 获取指定key
//            nowMap = (Map) nowMap.get(word);
//            // 存在，则判断是否为最后一个
//            if (nowMap != null) {
//                // 找到相应key，匹配标识+1
//                matchFlag++;
//                // 如果为最后一个匹配规则,结束循环，返回匹配标识数
//                if ("1".equals(nowMap.get("isEnd"))) {
//                    // 结束标志位为true
//                    flag = true;
//                    // 最小规则，直接返回,最大规则还需继续查找
//                    if (minMatchType == matchType) {
//                        break;
//                    }
//                }
//            }
//            // 不存在，直接返回
//            else {
//                break;
//            }
//        }
//        // 长度必须大于等于1，为词
//        if (matchFlag < 2 || !flag) {
//            matchFlag = 0;
//        }
//        return matchFlag;
//    }
//
//    private void sensitiveWordMapInit(String resourcePath) throws IOException {
//        InputStream ins = getClass().getResourceAsStream(resourcePath);
//        InputStreamReader reader = new InputStreamReader(ins, ENCODING);
//        BufferedReader bufferedReader = new BufferedReader(reader);
//        String word;
//        while ((word = bufferedReader.readLine()) != null) {
//            sensitiveWordDfaPut(word);
//        }
//        bufferedReader.close();
//        reader.close();
//    }
//
//    // 普通 Map
//    private void sensitiveWordPut(String word) {
//        int wordLength = word.length();
//        String newWord = (wordLength>replaceWord.length()) ? replaceWord : replaceWord.substring(0, wordLength);
//        sensitiveWordMap.put(word, newWord);
//    }
//
//    // DFA 结构
//    private void sensitiveWordDfaPut(String word) {
//        Map wordMap = sensitiveWordDfaMap;
//        for(int i = 0; i < word.length(); i++){
//            char wordChar = word.charAt(i);
//            if(wordMap.get(wordChar) == null) {
//                Map newWorMap = new HashMap<String,String>();
//                wordMap.put(wordChar, newWorMap);
//                wordMap = newWorMap;
//            }
//            else{
//                wordMap = (Map) wordMap.get(wordChar);
//            }
//        }
//        wordMap.put("isEnd", "1");
//    }
//}
