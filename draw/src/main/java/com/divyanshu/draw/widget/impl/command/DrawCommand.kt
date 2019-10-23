package com.divyanshu.draw.widget.impl.command

import com.divyanshu.draw.widget.contract.design.command.ICommand

class DrawCommand(private val container: ArrayList<Any>, private var draw: Any) : ICommand {
    override fun up() {
        container.add(draw)
    }

    override fun down() {
        container.remove(draw)
    }
}