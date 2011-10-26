/**
 * Created by IntelliJ IDEA.
 * User: arudin
 * Date: 10/26/11
 * Time: 2:31 PM
 */
(function($, window) {
    $.extend(window, {
                PCT:{}
            });
    $.extend(PCT, {
                init:function() {
                    $.ajax({
                                url:'pct.jsp',
                                data:{
                                    action:'getConfig'
                                },
                                success:function(data) {
                                    var resp;
                                    try {
                                        resp = $.parseJSON(data);
                                        PCT.tryToInit(resp);
                                    } catch(e) {
                                        PCT.initError();
                                    }
                                },
                                error:function(data) {
                                    PCT.initError();
                                }
                            });
                },
                tryToInit:function(initialData) {

                },
                initError:function() {
                    alert('Initialization failed');
                }
            });

})(jQuery, window);