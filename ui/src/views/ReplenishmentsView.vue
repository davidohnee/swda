<script setup lang="ts">
    import { ref, onMounted } from "vue";
    import { api } from "@/api";
    import type { ReplenishmentItem } from "@/types";

    const replenishments = ref<ReplenishmentItem[]>([]);

    const fetchReplenishments = async () => {
        replenishments.value = await api.replenishments.getAll();
    };

    onMounted(() => {
        fetchReplenishments();
    });
</script>
<template>
    <div class="replenishments">
        <h1>Replenishments</h1>
        <table>
            <thead>
                <tr>
                    <th>Tracking ID</th>
                    <th>Product Name</th>
                    <th>Quantity</th>
                    <th>Status</th>
                </tr>
            </thead>
            <tbody>
                <tr
                    v-for="item in replenishments"
                    :key="item.trackingId"
                >
                    <td>{{ item.trackingId }}</td>
                    <td>{{ item.product.name }}</td>
                    <td>{{ item.quantity }}</td>
                    <td>
                        <div
                            class="status"
                            :class="{
                                success: ['DONE', 'CONFIRMED'].includes(
                                    item.status
                                ),
                                info: item.status === 'PENDING',
                            }"
                        >
                            {{ item.status }}
                        </div>
                    </td>
                </tr>
            </tbody>
        </table>
    </div>
</template>
