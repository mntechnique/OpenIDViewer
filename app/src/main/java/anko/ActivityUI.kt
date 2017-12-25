package anko

import android.graphics.Color
import android.support.design.widget.CoordinatorLayout
import android.view.Gravity
import android.widget.LinearLayout
import com.mntechnique.openidviewer.MainActivity
import org.jetbrains.anko.*

import com.mntechnique.openidviewer.R
import org.jetbrains.anko.design.coordinatorLayout

/**
 * Generate with Plugin
 * @plugin Kotlin Anko Converter For Xml
 * @version 1.2.1
 */
class ActivityUI : AnkoComponent<MainActivity> {
	internal lateinit var sub: String
	internal lateinit var uname: String
	internal lateinit var given_name: String
	internal lateinit var family_name: String
	internal lateinit var email: String
	internal lateinit var cLayout: CoordinatorLayout

	constructor(sub: String, uname: String, given_name: String, family_name: String, email: String){
		this.sub = sub
		this.uname = uname
		this.given_name = given_name
		this.family_name = family_name
		this.email = email
	}

	fun stopProgress(){

	}
	override fun createView(ui: AnkoContext<MainActivity>) = with(ui) {
		coordinatorLayout {
			//tools:context = com.mntechnique.openidviewer.MainActivity //not support attribute
			linearLayout {
				orientation = LinearLayout.VERTICAL
				gravity = Gravity.CENTER
				progressBar {
					id = R.id.progressBar
				}.lparams(width = matchParent)
			}.lparams(width = matchParent, height = matchParent)

			linearLayout {
				id = R.id.llOpenID
				orientation = LinearLayout.VERTICAL
				padding = dip(5)
				//android:visibility = gone //not support attribute
				linearLayout {
					orientation = LinearLayout.HORIZONTAL
					padding = dip(5)
					textView {
						id = R.id.tvSubLabel
						text = "Sub"
					}.lparams(width = matchParent) {
						weight = 1f
					}
					textView {
						id = R.id.tvSub
						text = sub
					}.lparams(width = matchParent) {
						weight = 1f
					}
				}.lparams(width = matchParent)
				linearLayout {
					orientation = LinearLayout.VERTICAL
					view {
						backgroundColor = Color.parseColor("#ffaa00")
					}.lparams(width = matchParent, height = dip(2))
				}.lparams(width = matchParent)
				linearLayout {
					orientation = LinearLayout.HORIZONTAL
					padding = dip(5)
					textView {
						id = R.id.tvNameLabel
						text = "Name"
					}.lparams(width = matchParent) {
						weight = 1f
					}
					textView {
						id = R.id.tvName
						text = uname
					}.lparams(width = matchParent) {
						weight = 1f
					}
				}.lparams(width = matchParent)
				linearLayout {
					orientation = LinearLayout.VERTICAL
					view {
						backgroundColor = Color.parseColor("#ffaa00")
					}.lparams(width = matchParent, height = dip(2))
				}.lparams(width = matchParent)
				linearLayout {
					orientation = LinearLayout.HORIZONTAL
					padding = dip(5)
					textView {
						id = R.id.tvGivenNameLabel
						text = "Given Name"
					}.lparams(width = matchParent) {
						weight = 1f
					}
					textView {
						id = R.id.tvGivenName
						text = given_name
					}.lparams(width = matchParent) {
						weight = 1f
					}
				}.lparams(width = matchParent)
				linearLayout {
					orientation = LinearLayout.VERTICAL
					view {
						backgroundColor = Color.parseColor("#ffaa00")
					}.lparams(width = matchParent, height = dip(2))
				}.lparams(width = matchParent)
				linearLayout {
					orientation = LinearLayout.HORIZONTAL
					padding = dip(5)
					textView {
						id = R.id.tvFamNameLabel
						text = "Family Name"
					}.lparams(width = matchParent) {
						weight = 1f
					}
					textView {
						id = R.id.tvFamName
						text = family_name
					}.lparams(width = matchParent) {
						weight = 1f
					}
				}.lparams(width = matchParent)
				linearLayout {
					orientation = LinearLayout.VERTICAL
					view {
						backgroundColor = Color.parseColor("#ffaa00")
					}.lparams(width = matchParent, height = dip(2))
				}.lparams(width = matchParent)
				linearLayout {
					orientation = LinearLayout.HORIZONTAL
					padding = dip(5)
					textView {
						id = R.id.tvEmailLabel
						text = "Email"
					}.lparams(width = matchParent) {
						weight = 1f
					}
					textView {
						id = R.id.tvEmail
						text = email
					}.lparams(width = matchParent) {
						weight = 1f
					}
				}.lparams(width = matchParent)
				linearLayout {
					orientation = LinearLayout.VERTICAL
					view {
						backgroundColor = Color.parseColor("#ffaa00")
					}.lparams(width = matchParent, height = dip(2))
				}.lparams(width = matchParent)
			}.lparams(width = matchParent, height = matchParent)
		}
	}
}
