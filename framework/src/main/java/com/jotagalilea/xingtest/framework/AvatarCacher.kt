package com.jotagalilea.xingtest.framework

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Environment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.jotagalilea.xingtest.model.Repo
import java.io.File
import java.io.FileOutputStream

class AvatarCacher(
    private val context: Context
) {

    // <url remota, ruta del fichero>
    //TODO: Pensar otro sitio mejor para tener esto.
    val cachedAvatars: HashMap<String, String> = HashMap()


    //TODO: Repasar comentario
    /**
     * Si no se tiene, toma la imagen de url, la guarda en BD y guarda la ruta del archivo en el modelo.
     */
    fun cacheAvatar(repo: Repo){
        val url = repo.avatar_url
        val dir = context.getExternalFilesDir(Environment.getStorageDirectory().toString() + "/Avatares/")
        if (!dir?.exists()!!)
            dir.mkdirs()

        val path = dir.absolutePath
        val out = File(path + '/' + repo.login + ".png")
        repo.avatar_file = out.absolutePath
        val fos = FileOutputStream(out)

        if (!cachedAvatars.containsKey(url)) {
            Glide.with(context)
                .load(repo.avatar_url)
                .into(object : CustomTarget<Drawable>() {
                    override fun onResourceReady(
                        resource: Drawable,
                        transition: Transition<in Drawable>?
                    ) {
                        val bitmap = (resource as BitmapDrawable).bitmap
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
                        fos.flush()
                        fos.close()
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}

                })
        }
        else {
            fos.close()
        }
    }

}