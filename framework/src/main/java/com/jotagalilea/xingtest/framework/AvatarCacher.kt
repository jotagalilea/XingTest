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

    // Útil para tener un registro de los avatares ya descargados.
    // <url remota, ruta del fichero>
    val savedAvatars: HashMap<String, String> = HashMap()

    /**
     * Si no se tiene el avatar, lo toma de url, lo guarda en un archivo y guarda la ruta de este en el modelo.
     */
    fun cacheAvatar(repo: Repo) {
        val dir = context.getExternalFilesDir(Environment.getStorageDirectory().toString() + "/Avatares/")
        if (!dir?.exists()!!)
            dir.mkdirs()

        val path = dir.absolutePath
        val out = File(path + '/' + repo.login + ".png")
        repo.avatar_file = out.absolutePath

        Glide.with(context)
            .load(repo.avatar_url)
            .into(object : CustomTarget<Drawable>() {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    val fos = FileOutputStream(out)
                    val bitmap = (resource as BitmapDrawable).bitmap
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
                    fos.flush()
                    savedAvatars[repo.avatar_url] = repo.avatar_file
                    fos.close()
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
    }
}