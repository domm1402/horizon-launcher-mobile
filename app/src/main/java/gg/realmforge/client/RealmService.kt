package gg.realmforge.client
import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
class RealmService(context:Context){
 private val prefs=context.getSharedPreferences("bedrock_account",0)
 private val base="https://pocket.realms.minecraft.net"
 private fun call(path:String,method:String="GET",body:String?=null):JSONObject{
  val u=URL(base+path).openConnection() as HttpURLConnection
  u.requestMethod=method;u.setRequestProperty("Authorization","XBL3.0 x="+prefs.getString("xuid","")+";"+prefs.getString("realm_xsts",""))
  u.setRequestProperty("User-Agent","Minecraft Android");u.setRequestProperty("Client-Version","1.26.0");u.setRequestProperty("Accept","application/json")
  if(body!=null){u.doOutput=true;u.setRequestProperty("Content-Type","application/json");u.outputStream.use{it.write(body.toByteArray())}}
  val code=u.responseCode;val stream=if(code in 200..299)u.inputStream else u.errorStream
  val text=stream?.bufferedReader()?.use{it.readText()}?: "";u.disconnect()
  if(code !in 200..299)throw Exception("Realms HTTP $code: $text")
  return if(text.isBlank())JSONObject() else JSONObject(text)
 }
 data class Realm(val id:String,val name:String,val owner:String,val players:Int,val max:Int,val state:String)
 fun list():List<Realm>{val j=call("/worlds");val a=j.optJSONArray("servers")?:JSONArray();return (0 until a.length()).map{val o=a.getJSONObject(it);Realm(o.optString("id"),o.optString("name"),o.optString("owner"),o.optInt("players"),o.optInt("maxPlayers"),o.optString("state"))}}
 /** Returns server connection metadata. NetherNet signaling/session setup follows this response. */
 fun joinInfo(id:String)=call("/worlds/$id/join")
 fun invite(code:String)=call("/ invites/$code".replace(" ",""))
}
