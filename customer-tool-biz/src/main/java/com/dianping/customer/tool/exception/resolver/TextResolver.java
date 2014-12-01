package com.dianping.customer.tool.exception.resolver;

import com.dianping.customer.tool.exception.UserShopTerritoryException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Map;

/**
 * Created by yangjie on 12/1/14.
 */
public class TextResolver implements HandlerExceptionResolver {

    static class TextView implements View  {
        private String msg;

        public TextView(String msg) {
            this.msg = msg;
        }

        @Override
        public String getContentType() {
            return "text";
        }

        @Override
        public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
            response.setContentType(getContentType());
            response.setCharacterEncoding("UTF-8");


            PrintWriter writer = response.getWriter();
            writer.print(this.msg);
            writer.flush();
        }
    }

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        if (ex instanceof UserShopTerritoryException) {
            return new ModelAndView(new TextView(ex.getMessage()));
        }
        return null;
    }
}
