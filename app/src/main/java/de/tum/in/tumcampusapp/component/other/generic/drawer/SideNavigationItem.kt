package de.tum.`in`.tumcampusapp.component.other.generic.drawer

import android.app.Activity

data class SideNavigationItem(val titleRes: Int,
                              val iconRes: Int,
                              val activity: Class<out Activity>? = null,
                              val needsTUMOAccess: Boolean = false,
                              val needsChatAccess: Boolean = false)
