package gg.realmforge.client
import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.UUID

/** Local world metadata store. Actual terrain/chunk data is written by the engine layer. */
class LocalWorlds(context:Context){
 private val root=File(context.filesDir,"worlds").apply{mkdirs()}
 data class World(val id:String,val name:String,val created:Long)
 fun all():List<World>{val a=JSONArray(File(root,"index.json").takeIf{it.exists()}?.readText()?: "[]");return (0 until a.length()).map{val o=a.getJSONObject(it);World(o.getString("id"),o.getString("name"),o.getLong("created"))}}
 fun create(name:String):World{require(name.trim().isNotEmpty()){"World name cannot be blank"};val w=World(UUID.randomUUID().toString(),name.trim(),System.currentTimeMillis());File(root,w.id).mkdirs();write(all()+w);return w}
 fun delete(id:String){File(root,id).deleteRecursively();write(all().filterNot{it.id==id})}
 private fun write(worlds:List<World>){val a=JSONArray();worlds.forEach{a.put(JSONObject().put("id",it.id).put("name",it.name).put("created",it.created))};File(root,"index.json").writeText(a.toString())}
 fun worldDirectory(id:String)=File(root,id)
}
