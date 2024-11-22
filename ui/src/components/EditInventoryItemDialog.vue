<script setup lang="ts">
    import { computed, onMounted, ref } from "vue";
    import { api } from "@/api";
    import type { InventoryItem } from "@/types";

    const item = ref<InventoryItem>();
    const dialog = ref<HTMLDialogElement>();

    const emit = defineEmits(["close"]);

    const open = (inventoryItem: InventoryItem) => {
        dialog.value?.showModal();
        item.value = JSON.parse(JSON.stringify(inventoryItem));
    };

    const close = () => {
        dialog.value?.close();
        emit("close");
    };

    defineExpose({ open });

    const apply = async () => {
        await api.inventory.update(item.value!.product.id, {
            quantity: item.value!.quantity,
            replenishmentThreshold: item.value!.replenishmentThreshold,
        });
        close();
    };
</script>
<template>
    <dialog ref="dialog">
        <span
            class="material-symbols-rounded close"
            @click="dialog?.close()"
        >
            close
        </span>

        <template v-if="item">
            <h1>Editing {{ item.product.name }}</h1>

            <div class="options">
                <div class="option">
                    <label for="quantity">Quantity</label>
                    <input
                        type="number"
                        id="quantity"
                        v-model="item.quantity"
                    />
                </div>
                <div class="option">
                    <label for="replenishmentThreshold">
                        Replenishment Threshold
                    </label>
                    <input
                        type="number"
                        id="replenishmentThreshold"
                        v-model="item.replenishmentThreshold"
                    />
                </div>
            </div>
        </template>
        <button
            class="success"
            @click="apply"
        >
            <span class="material-symbols-rounded">done</span>
            Save
        </button>
    </dialog>
</template>
<style scoped>
    dialog[open],
    dialog[open] > div {
        outline: none;
        display: flex;
        flex-direction: column;
        width: 100%;
        max-width: 40vw;
        overflow: hidden;
        gap: 1em;

        & input {
            width: unset;
        }
    }

    .options {
        display: flex;
        flex-direction: row;
        gap: 1em;

        .option {
            display: grid;
            grid-template-columns: 1fr 20ch;
            gap: 1em;
            align-items: center;
            border: none;
            border-radius: 0;

            &:not(:last-child) {
                border-bottom: 2px solid var(--bg-base-lt);
            }
        }
    }
</style>
