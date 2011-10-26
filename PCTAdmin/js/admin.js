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
        format:'xml'
    });
    $.extend(PCT, {
        init:function() {
            $.ajax({
                url:'data',
                data:{
                    action:'getConfig',
                    format:PCT.format
                },
                dataType:PCT.format,
                success:function(data, textStatus, jqXHR) {
                    PCT.tryToInit(data);
                },
                error:function(data) {
                    PCT.initError();
                }
            });
        },
        tryToInit:function(initialData) {
            document.evaluate("/root/")
            console.log(initialData);
        },
        initError:function() {
            alert('Initialization failed');
        }
    });

})(jQuery, window);