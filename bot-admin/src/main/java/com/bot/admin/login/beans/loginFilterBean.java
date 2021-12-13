package com.bot.admin.login.beans;

//@Component
//public class loginFilterBean {
//    @Bean
//    LoginFilter loginFilter()throws Exception{
//        LoginFilter loginFilter = new LoginFilter();
//        loginFilter.setAuthenticationSuccessHandler(new AuthenticationSuccessHandler() {
//            @Override
//            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
//                response.setContentType("application/json;charset=utf-8");
//                PrintWriter out = response.getWriter();
//                User user = (User) authentication.getPrincipal();
//                user.setPassword(null);
//                RespBean ok = RespBean.ok("登录成功!", user);
//                String s = new ObjectMapper().writeValueAsString(ok);
//                out.write(s);
//                out.flush();
//                out.close();
//            }
//        });
//        loginFilter.setAuthenticationFailureHandler(new AuthenticationFailureHandler() {
//            @Override
//            public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
//                response.setContentType("application/json;charset=utf-8");
//                PrintWriter out = response.getWriter();
//                RespBean respBean = RespBean.error(exception.getMessage());
//                if (exception instanceof LockedException) {
//                    respBean.setMsg("账户被锁定，请联系管理员!");
//                } else if (exception instanceof CredentialsExpiredException) {
//                    respBean.setMsg("密码过期，请联系管理员!");
//                } else if (exception instanceof AccountExpiredException) {
//                    respBean.setMsg("账户过期，请联系管理员!");
//                } else if (exception instanceof DisabledException) {
//                    respBean.setMsg("账户被禁用，请联系管理员!");
//                } else if (exception instanceof BadCredentialsException) {
//                    respBean.setMsg("用户名或者密码输入错误，请重新输入!");
//                }
//                out.write(new ObjectMapper().writeValueAsString(respBean));
//                out.flush();
//                out.close();
//            }
//        });
//        loginFilter.setAuthenticationManager(authenticationManagerBean());
//        loginFilter.setFilterProcessesUrl("/doLogin");
//        return loginFilter;
//    }
//}
