package info.bugnode.model

object Url {
  /**
   * fun web URL
   * @return String
   */
  fun web(subDomain: String): String {
    return "http://192.168.1.73/api/${subDomain.replace(".", "/")}"
  }

  /**
   * fun doge URL
   * @return String
   */
  fun doge(): String {
    return "https://www.999doge.com/api/web.aspx"
  }
}