package com.glureau.compiler.test.todo

import android.content.Intent
import android.view.View
import com.glureau.compiler.test.OrgMembersActivity
import timber.log.Timber

object Router {
    fun loadMembersActivity(view: View, org: String) {
        val context = view.context
        val intent = Intent(context, OrgMembersActivity::class.java)
        intent.putExtra(OrgMembersActivity.INTENT_PARAM_ORGANIZATION, org)
        Timber.e("Route to $intent WITH ${intent.getStringExtra(OrgMembersActivity.INTENT_PARAM_ORGANIZATION)}")
        context.startActivity(intent)
    }
}
