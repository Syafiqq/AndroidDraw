package com.divyanshu.draw.widget.impl.command

import com.divyanshu.draw.widget.contract.design.command.ICommand
import com.divyanshu.draw.widget.mode.PathMode

class ClearCommand(private val container: ArrayList<PathMode>) : ICommand {
    private val holder = ArrayList<PathMode>()
    override fun up() {
        with(container) {
            holder.addAll(this)
            clear()
        }
    }

    override fun down() {
        with(holder) {
            container.addAll(this)
            clear()
        }
    }
}