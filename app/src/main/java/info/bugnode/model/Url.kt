package info.bugnode.model

object Url {
  /**
   * fun web URL
   * @return String
   */
  fun web(subDomain: String): String {
    return "http://10.0.2.2:8000/api/${subDomain.replace(".", "/")}"
  }

  /**
   * fun doge URL
   * @return String
   */
  fun doge(): String {
    return "https://www.999doge.com/api/web.aspx"
  }
}