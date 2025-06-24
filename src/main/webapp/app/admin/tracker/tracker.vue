<template>
  <div>
    <h2 id="tracker-page-heading" data-cy="trackerPageHeading">用户实时状态</h2>

    <div class="table-responsive">
      <table class="table table-striped" data-cy="trackerTable">
        <thead>
          <tr>
            <th>用户</th>
            <th>IP地址</th>
            <th>当前页面</th>
            <th>时间</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="activity of activities" :key="activity.sessionId">
            <td>{{ activity.userLogin }}</td>
            <td>{{ activity.ipAddress }}</td>
            <td>{{ activity.page }}</td>
            <td>{{ formatDate(activity.time) }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script lang="ts" src="./tracker.component.ts"></script>
