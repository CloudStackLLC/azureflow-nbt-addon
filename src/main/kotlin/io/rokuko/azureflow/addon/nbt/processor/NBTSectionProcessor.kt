package io.rokuko.azureflow.addon.nbt.processor

import io.rokuko.azureflow.features.config.ItemConfiguration
import io.rokuko.azureflow.features.config.SectionProcessor
import io.rokuko.azureflow.features.item.factory.AzureFlowItemFactory

object NBTSectionProcessor: SectionProcessor {
    override val path = "nbt"
    override val priority: Int = 100

    override fun process(config: ItemConfiguration, factory: AzureFlowItemFactory): Boolean {
        TODO("Not yet implemented")
    }
}