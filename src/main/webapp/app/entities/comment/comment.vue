<template>
  <div>
    <h2 id="comment-heading">Comments</h2>
    <b-table :items="comments" :fields="['id', 'content']" responsive="sm"></b-table>
  </div>
</template>

<script lang="ts" src="./comment.component.ts"></script>
