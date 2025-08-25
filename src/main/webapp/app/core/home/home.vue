<template>
  <div class="home row">
    <div class="col-md-3">
      <span class="hipster img-fluid rounded"></span>
    </div>
    <div class="col-md-9">
      <h1 class="display-4">欢迎来到 aiRead!</h1>
      <p class="lead">这里是首页</p>

      <div>
        <div class="alert alert-success" v-if="authenticated">
          <span v-if="username">您目前是以 "{{ username }}" 账号登录.</span>
        </div>

        <div class="alert alert-warning" v-if="!authenticated">
          <span>如果您要</span>
          <a class="alert-link" @click="showLogin()">登录</a
          ><span>, 您可以使用默认账号:<br />- 管理员 (账号="admin"和密码="admin") <br />- 普通用户 (账号="user"和密码="user").</span>
        </div>
        <div class="alert alert-warning" v-if="!authenticated">
          <span>您还没有账号</span>&nbsp;
          <router-link class="alert-link" to="/register">注册一个新账号</router-link>
        </div>
      </div>

      <p>欢迎使用 aiRead，您可以在这里快速访问常用功能:</p>

      <ul>
        <li><router-link to="/novel">文件上传</router-link></li>
        <li><router-link to="/chapter-content">文章阅读</router-link></li>
        <li><router-link to="/reading-voucher">优惠券查看</router-link></li>
        <li><router-link to="/voucher">抢优惠券</router-link></li>
      </ul>
    </div>
  </div>
</template>

<script lang="ts" src="./home.component.ts"></script>
