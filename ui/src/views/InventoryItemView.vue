<script setup lang="ts">
    import { computed, onMounted, ref, watch } from "vue";
    import type { InventoryItem } from "@/types";
    import EditInventoryItemDialog from "@/components/EditInventoryItemDialog.vue";
    import ErrorLoader from "@/components/ErrorLoader.vue";
    import { useInventoryStore } from "@/stores/inventory";
    import { useRoute } from "vue-router";

    const inventory = useInventoryStore();
    const inventoryItem = ref<InventoryItem>();
    const editDialog = ref<typeof EditInventoryItemDialog>();
    const route = useRoute();
    const productId = computed(() => {
        return Number(route.params.id);
    });

    const edit = () => {
        editDialog.value!.open(inventoryItem.value);
    };

    const updateItem = async () => {
        inventoryItem.value = inventory.getByProductId(productId.value);
    };

    onMounted(updateItem);
    watch(productId, updateItem);
    watch(() => inventory.inventory, updateItem);
</script>

<template>
    <div class="inventoryItem">
        <div class="title">
            <h2>Product ID: {{ productId }}</h2>
            <div
                v-if="inventoryItem"
                class="status"
                :class="{
                    success:
                        inventoryItem.quantity >
                        inventoryItem.replenishmentThreshold,
                    warning:
                        inventoryItem.quantity <=
                            inventoryItem.replenishmentThreshold &&
                        inventoryItem.quantity > 0,
                    error: inventoryItem.quantity === 0,
                }"
            >
                {{
                    inventoryItem.quantity >
                    inventoryItem.replenishmentThreshold
                        ? "In Stock"
                        : inventoryItem.quantity === 0
                        ? "Out of Stock"
                        : "Low Stock"
                }}
            </div>
        </div>
        <ErrorLoader :content="inventory.response">
            <div>
                <EditInventoryItemDialog
                    @close="inventory.refresh"
                    ref="editDialog"
                />
                <div class="distribute">
                    <main>
                        <h3>{{ inventoryItem?.product.name }}</h3>
                        <span
                            >Price:
                            <strong>
                                ${{ inventoryItem?.product.price }}
                            </strong>
                        </span>
                    </main>
                    <aside>
                        <div class="card">
                            <button
                                class="edit"
                                @click="edit"
                            >
                                Edit
                            </button>
                            <h3>Inventory</h3>

                            <p>Quantity: {{ inventoryItem?.quantity }}</p>
                            <p>
                                Replenishment Threshold:
                                {{ inventoryItem?.replenishmentThreshold }}
                            </p>
                        </div>
                    </aside>
                </div>
            </div>
        </ErrorLoader>
    </div>
</template>

<style scoped>
    .title {
        display: flex;
        align-items: center;
        gap: 1em;

        .status {
            font-size: 0.8rem;
        }
    }

    .card {
        padding: 1em;
        position: relative;

        & h3 {
            margin: 0;
        }
    }

    .inventoryItem {
        display: flex;
        flex-direction: column;
    }

    .edit {
        position: absolute;
        top: 1em;
        right: 1em;
    }

    .distribute {
        display: flex;
        flex-direction: row;
        flex-wrap: wrap;
        gap: 1em;
        margin-top: 1em;
    }

    main {
        flex: 1;
    }

    aside {
        flex: 1;
        max-width: 30%;
        min-width: 50ch;
    }
</style>
