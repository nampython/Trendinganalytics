package com.sentiment.worker.amazonService;

import com.amazonaws.services.comprehend.AmazonComprehend;
import com.amazonaws.services.comprehend.model.DetectSentimentRequest;
import com.amazonaws.services.comprehend.model.DetectSentimentResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class AmazonDetecSentiment {

    @Autowired
    AmazonComprehend amazonComprehend;


    /**
     * return {
     *  setiment: "NEUTRAL"
     *  SentimentScore@115 "{Positive: 0.011440417,Negative: 0.00800881,Neutral: 0.98032975,Mixed: 2.2097652E-4}"
     * }
     * @param tweet
     * @return
     */

    public HashMap<String, Float> detectSentiments(String tweet) {

        log.info("Calling DetectSentiment for ::" + tweet);

        DetectSentimentRequest detectSentimentRequest = new DetectSentimentRequest().withText(tweet)
                .withLanguageCode("en");

        /**
         *
         {
         "SentimentScore": {
         "Mixed": 0.014585512690246105,
         "Positive": 0.31592071056365967,
         "Neutral": 0.5985543131828308,
         "Negative": 0.07093945890665054
         },
         "Sentiment": "NEUTRAL",
         "LanguageCode": "en"
         }
         */
        DetectSentimentResult detectSentimentResult = amazonComprehend.detectSentiment(detectSentimentRequest);


        log.info(String.valueOf(detectSentimentResult));
        log.info("End of DetectSentiment\n");
        log.info("Done");
        return mapSentiment(detectSentimentResult);
    }

    /**
     *
     * @param detectSentimentResult
     * @return ["Mixed": 0.003, "Negative": 1.782, "Positive": 0.033, "Neutral": 1.782]
     */
    private HashMap<String, Float> mapSentiment(DetectSentimentResult detectSentimentResult) {

        DecimalFormat df = new DecimalFormat("#.###");

        Map<String, Float> sentimentMap = new HashMap<>();

        sentimentMap.put("Mixed", Float.valueOf(df.format(detectSentimentResult.getSentimentScore().getMixed() * 100)));
        sentimentMap.put("Negative", Float.valueOf(df.format(detectSentimentResult.getSentimentScore().getNegative() * 100)));
        sentimentMap.put("Neutral", Float.valueOf(df.format(detectSentimentResult.getSentimentScore().getNeutral() * 100)));
        sentimentMap.put("Positive", Float.valueOf(df.format(detectSentimentResult.getSentimentScore().getPositive() * 100)));

        return (HashMap<String, Float>) sentimentMap;
    }

}
