<script setup lang="ts">
    import { ref } from "vue";
    import { api } from "@/api";
    import { useInventoryStore } from "@/stores/inventory";
    import type { OrderCreate } from "@/types";
    import { useCustomerStore } from "@/stores/customers";

    const inventory = useInventoryStore();
    const customers = useCustomerStore();
    const dialog = ref<HTMLDialogElement>();

    const order = ref<OrderCreate>({
        dateTime: new Date(),
        orderItems: [],
        customerId: "",
        sellerId: "",
        destinationId: "",
        orderType: "CUSTOMER_ORDER",
    });

    const emit = defineEmits(["close"]);

    const open = () => {
        dialog.value?.showModal();
    };

    const close = () => {
        dialog.value?.close();
        emit("close");
    };

    defineExpose({ open });

    const requestValid = () => {
        return (
            order.value.customerId !== "" &&
            order.value.sellerId !== "" &&
            order.value.destinationId !== "" &&
            order.value.orderItems.length > 0 &&
            order.value.orderItems.every(
                (x) => x.productId !== 0 && x.quantity > 0
            )
        );
    };

    const apply = async () => {
        await api.orders.create(order.value);
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

        <h1>Order</h1>

        <div class="options">
            <div class="option card combo">
                <span>Items</span>
                <div
                    v-for="(item, index) in order.orderItems"
                    :key="item.productId"
                    class="orderItem"
                >
                    <select v-model="item.productId">
                        <option
                            v-for="item in inventory.inventory"
                            :key="item.product.id"
                            :value="item.product.id"
                        >
                            {{ item.product.name }}
                        </option>
                    </select>
                    <input
                        id="items"
                        type="number"
                        v-model="item.quantity"
                    />
                    <button @click="order.orderItems.splice(index, 1)">
                        <span class="material-symbols-rounded">close</span>
                    </button>
                </div>
                <button
                    @click="
                        order.orderItems.push({ productId: 0, quantity: 1 })
                    "
                >
                    <span class="material-symbols-rounded">add</span>
                </button>
            </div>
            <div class="option">
                <label for="customer">Customer ID</label>
                <select
                    id="customer"
                    v-model="order.customerId"
                >
                    <option
                        v-for="item in customers.customers"
                        :key="item.id"
                        :value="item.id"
                    >
                        {{ item.firstName }} {{ item.familyName }}
                    </option>
                </select>
            </div>
            <div class="option">
                <label for="seller">Seller ID</label>
                <input
                    id="seller"
                    v-model="order.sellerId"
                />
            </div>
            <div class="option">
                <label for="destination">Destination ID</label>
                <input
                    id="destination"
                    v-model="order.destinationId"
                />
            </div>
        </div>
        <button
            class="success"
            @click="apply"
            :disabled="!requestValid()"
        >
            <span class="material-symbols-rounded">check</span>
            Order
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

            &.combo {
                display: flex;
                flex-direction: column;
                width: 100%;
                align-items: stretch;
            }

            &.card {
                padding: 1em;
                border-radius: 0.5em;
                width: calc(100% - 2em - 2px);
                border: 1px solid var(--border);
                background: var(--bg-mute);
            }
        }
    }

    .orderItem {
        display: grid;
        grid-template-columns: 1fr 1fr 5ch;
        gap: 1em;
        align-items: center;

        & button {
            border: none;
            background: none;
            cursor: pointer;
        }
    }

    h1 {
        font-size: 2rem;
    }
</style>
