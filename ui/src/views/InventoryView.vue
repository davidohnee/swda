<script setup lang="ts">
    import { ref } from "vue";
    import type { InventoryItem } from "@/types";
    import EditInventoryItemDialog from "@/components/EditInventoryItemDialog.vue";
    import ErrorLoader from "@/components/ErrorLoader.vue";
    import { useInventoryStore } from "@/stores/inventory";

    const inventory = useInventoryStore();
    const editDialog = ref<typeof EditInventoryItemDialog>();

    const edit = (item: InventoryItem) => {
        editDialog.value!.open(item);
    };
</script>

<template>
    <div class="inventory">
        <h1>Inventory</h1>
        <ErrorLoader :content="inventory.response">
            <div>
                <EditInventoryItemDialog
                    @close="inventory.refresh"
                    ref="editDialog"
                />
                <table>
                    <thead>
                        <tr>
                            <th>Product ID</th>
                            <th>Product Name</th>
                            <th>Price</th>
                            <th>Quantity</th>
                            <th>Status</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr
                            v-for="item in inventory.inventory"
                            :key="item.product.id"
                        >
                            <td>{{ item.product.id }}</td>
                            <td>{{ item.product.name }}</td>
                            <td>${{ item.product.price }}</td>
                            <td class="multi-line">
                                {{ item.quantity }}
                                <span class="muted">
                                    Replenish when below:
                                    {{ item.replenishmentThreshold }}
                                </span>
                            </td>
                            <td>
                                <div
                                    class="status"
                                    :class="{
                                        success:
                                            item.quantity >
                                            item.replenishmentThreshold,
                                        warning:
                                            item.quantity <=
                                                item.replenishmentThreshold &&
                                            item.quantity > 0,
                                        error: item.quantity === 0,
                                    }"
                                >
                                    {{
                                        item.quantity >
                                        item.replenishmentThreshold
                                            ? "In Stock"
                                            : item.quantity === 0
                                            ? "Out of Stock"
                                            : "Low Stock"
                                    }}
                                </div>
                            </td>
                            <td>
                                <span
                                    @click="edit(item)"
                                    class="material-symbols-rounded clickable"
                                    >edit</span
                                >
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </ErrorLoader>
    </div>
</template>
