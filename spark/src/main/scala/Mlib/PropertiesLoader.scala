package Mlib

import com.typesafe.config.{Config, ConfigFactory}

/**
 * Exposes all the key-value pairs as properties object using Config object of Typesafe Config project.
 */
object PropertiesLoader {
    private val conf: Config = ConfigFactory.load("application.conf")

    val sentiment140TrainingFilePath: String = conf.getString("SENTIMENT140_TRAIN_DATA_ABSOLUTE_PATH");
    val sentiment140TestingFilePath: String = conf.getString("SENTIMENT140_TEST_DATA_ABSOLUTE_PATH")

    val naiveBayesModelPath: String = conf.getString("NAIVEBAYES_MODEL_ABSOLUTE_PATH")
    val nltkStopWords: String = conf.getString("NLTK_STOPWORDS_FILE_NAME ")

    val modelAccuracyPath: String = conf.getString("NAIVEBAYES_MODEL_ACCURACY_ABSOLUTE_PATH ")
}
