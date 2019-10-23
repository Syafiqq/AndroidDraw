package com.divyanshu.draw.widget.impl.command

import com.divyanshu.draw.widget.contract.design.command.ICommand
import com.divyanshu.draw.widget.mode.PathMode

class DrawCommand(private val container: ArrayList<PathMode>, private var draw: PathMode) : ICommand {
    override fun up() {
        container.add(draw)
    }

    override fun down() {
        container.remove(draw)
    }
}