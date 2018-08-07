# MyButterKnife

一个模仿ButterKnife的注入框架，使用APT实现，在编译的时候解析注解，避免了使用反射带来的性能问题

目前实现的功能：

### @BindView

### @OnClick

使用方法跟ButterKnife一样，注册的使用使用`MyButterKnife.inject(this);`
