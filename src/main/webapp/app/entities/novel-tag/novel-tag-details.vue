<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="novelTag">
        <h2 class="jh-entity-heading" data-cy="novelTagDetailsHeading"><span>Novel Tag</span> {{ novelTag.id }}</h2>
        <dl class="row jh-entity-details">
          <dt>
            <span>Tag Id</span>
          </dt>
          <dd>
            <span>{{ novelTag.tagId }}</span>
          </dd>
          <dt>
            <span>Tag Name</span>
          </dt>
          <dd>
            <span>{{ novelTag.tagName }}</span>
          </dd>
          <dt>
            <span>Category</span>
          </dt>
          <dd>
            <span>{{ novelTag.category }}</span>
          </dd>
          <dt>
            <span>Is Hot</span>
          </dt>
          <dd>
            <span>{{ novelTag.isHot }}</span>
          </dd>
          <dt>
            <span>Create Time</span>
          </dt>
          <dd>
            <span v-if="novelTag.createTime">{{ formatDateLong(novelTag.createTime) }}</span>
          </dd>
          <dt>
            <span>Update Time</span>
          </dt>
          <dd>
            <span v-if="novelTag.updateTime">{{ formatDateLong(novelTag.updateTime) }}</span>
          </dd>
        </dl>
        <button type="submit" @click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span>返回</span>
        </button>
        <router-link v-if="novelTag.id" :to="{ name: 'NovelTagEdit', params: { novelTagId: novelTag.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span>编辑</span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./novel-tag-details.component.ts"></script>
