<script setup lang="ts">
    import { ref, onMounted, computed } from "vue";
    import { api } from "@/api";
    import type { Customer } from "@/types";
    import ErrorLoader from "@/components/ErrorLoader.vue";
    import type { ApiResponse } from "@/api/helper";

    const response = ref<ApiResponse<Customer[]>>();
    const customers = computed(
        () => (response.value?.data ?? []) as Customer[]
    );

    const fetchCustomers = async () => {
        response.value = await api.cutomers.getAll();
    };

    onMounted(() => {
        fetchCustomers();
    });
</script>
<template>
    <div class="customers">
        <h1>Customers</h1>
        <ErrorLoader :content="response">
            <div>
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>First Name</th>
                            <th>Family Name</th>
                            <th>Address</th>
                            <th>Contact Info</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr
                            v-for="item in customers"
                            :key="item.id"
                        >
                            <td>{{ item.id }}</td>
                            <td>{{ item.firstName }}</td>
                            <td>{{ item.familyName }}</td>
                            <td>{{ item.address }}</td>
                            <td>
                                <ul>
                                    <li>Email: {{ item.contactInfo.email }}</li>
                                    <li>Phone {{ item.contactInfo.phone }}</li>
                                </ul>
                            </td>
                        </tr>
                    </tbody>
                </table>
                <p
                    class="muted center"
                    v-if="customers.length === 0"
                >
                    <em> No customers found. </em>
                </p>
            </div>
        </ErrorLoader>
    </div>
</template>
