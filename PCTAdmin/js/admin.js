/**
 * Created by IntelliJ IDEA.
 * User: arudin
 * Date: 10/26/11
 * Time: 2:31 PM
 */
(function($, window, qq) {
    qq.UploadHandlerXhr.isSupported = function() {
        return false;
    };

    $.extend(window, {
        PCT:{}
    });

    $.extend(PCT, {
        timeout:10000,
        timer:10000,
        location:'/',
        animation:'blind',
        format:'xml',
        getConfiguration:function(src, pwd) {
            //var pwd = prompt("Provide a password to encrypt your configuration", "");
            pwd = src ? document.getElementById($(src).attr('pwdid')).value : pwd;
            if(!pwd || pwd==''){
                alert('Please, provide a password to encrypt your configuration');
                return false;
            }
            PCT.sendData(PCT.location, (src ? 'timestamp=' + $(src).attr('cfg') + '&': '') + 'action=downloadConfig&pwd=' + $().md5(pwd));
            pwd = null;
            return true;
        },
        releaseConfig:function(src) {
            if (PCT.checker) {
                PCT.lockScreen();
                PCT.checker(function() {
                    $.ajax({
                        url:PCT.location,
                        type:'POST',
                        data:{
                            action:'releaseConfig',
                            timestamp:$(src).attr('cfg')
                        },
                        /*success:function(data, textStatus, jqXHR) {
                         },
                         error:function(jqXHR, textStatus, errorThrown) {
                         if (error)error();
                         },*/
                        complete:function() {
                            window.location = '/';
                        }
                    });
                }, function(name) {
                    alert("Configuration locked by " + name);
                    PCT.unlockScreen();
                }, function() {
                }, function() {
                    alert("Couldn't connect to server. Try again later.");
                    PCT.unlockScreen();
                });
            }
        },
        getHome:function(dest, lock, callback) {
            if (lock) {
                PCT.lockScreen();
            }
            $.ajax({
                url:PCT.location,
                type:'POST',
                data:{
                    action:'getHome'
                },
                success:function(data, textStatus, jqXHR) {
                    $(dest).empty().html(data);
                },
                error:function(jqXHR, textStatus, errorThrown) {
                    alert('Loading failed: ' + textStatus);
                },
                statusCode: {
                    551: function() {
                        alert('File reading error.');
                    },
                    552: function() {
                        alert('Config validation error.');
                    }
                },
                complete:function() {
                    if (lock) {
                        PCT.unlockScreen();
                    }
                    if (callback) {
                        callback();
                    }
                }
            });
        },
        getSystem:function(dest, lock) {
            if (lock) {
                PCT.lockScreen();
            }
            $.ajax({
                url:PCT.location,
                type:'POST',
                data:{
                    action:'imageFiles'
                },
                success:function(data, textStatus, jqXHR) {
                    $(dest).empty().html(data);
                },
                error:function(jqXHR, textStatus, errorThrown) {
                    alert('Loading failed: ' + textStatus);
                },
                complete:function() {
                    if (lock) {
                        PCT.unlockScreen();
                    }
                }
            });
        },
        loadConfig:function(src) {
            if (PCT.checker) {
                if (confirm('Do you want to load configuration from ' + $(src).attr('date') + '?')) {
                    PCT.lockScreen();
                    PCT.checker(function() {
                        $.ajax({
                            url:PCT.location,
                            type:'POST',
                            data:{
                                action:'loadBackup',
                                file:$(src).attr('cfg')
                            },
                            /*success:function(data, textStatus, jqXHR) {
                             },
                             error:function(jqXHR, textStatus, errorThrown) {
                             if (error)error();
                             },*/
                            complete:function() {
                                window.location = '/';
                            }
                        });
                    }, function(name) {
                        alert("Configuration locked by " + name);
                        PCT.unlockScreen();
                    }, function() {
                    }, function() {
                        alert("Couldn't connect to server. Try again later.");
                        PCT.unlockScreen();
                    });
                }
            }
        },
        checkStatus:function(ifLockedByYou, ifLocked, ifNotLocked, complete, error) {
            $.ajax({
                url:PCT.location,
                type:'POST',
                data:{
                    action:'checkStatus'
                },
                dataType:'json',
                success:function(data, textStatus, jqXHR) {
                    if (!data.locked) {
                        if (ifNotLocked) {
                            ifNotLocked();
                        }
                    } else {
                        if (data.by != null) {
                            if (ifLocked)ifLocked(data.by);
                        } else {
                            if (ifLockedByYou)ifLockedByYou();
                        }
                    }
                },
                error:function(jqXHR, textStatus, errorThrown) {
                    if (error)error();
                },
                complete:function() {
                    if (complete)complete();
                }
            });
        },
        randomString:function (string_length) {
            var chars = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXTZ';
            var randomstring = '';
            for (var i = 0; i < string_length; i++) {
                var rnum = Math.floor(Math.random() * chars.length);
                randomstring += chars.substring(rnum, rnum + 1);
            }
            return randomstring;
        },
        lockScreen : function() {
            if (!PCT.lockCounter) {
                $.extend(PCT, {
                    lockCounter : 1
                });
            } else {
                PCT.lockCounter++;
            }
            PCT.lockDiv.updateView();
            $(PCT.lockDiv).show();
        },
        unlockScreen : function() {
            PCT.lockCounter--;
            if (PCT.lockCounter <= 0) {
                PCT.lockCounter = 0;
                $(PCT.lockDiv).hide();
            }
        },
        preventDefault : function(e) {
            e.preventDefault();
            return false;
        },
        updateViewCallbacks : new Array(),
        updateView : function() {
            for (key in PCT.updateViewCallbacks) {
                PCT.updateViewCallbacks[key]();
            }
        },
        disableAnimation:function() {
            $.fx.off = true;
        },
        template:function(template) {
            $.extend(this, {
                dom:$('<div></div>').append($(template).contents()),
                getCopy:function() {
                    var retDom = this.dom.clone();
                    $('input.validate', retDom).add('textarea.validate', retDom).bind('error',
                        function() {
                            $(this).addClass('error');
                            $(this).keypress(function(e) {
                                var keycode;
                                if (window.event)
                                    keycode = window.event.keyCode;
                                else if (e)
                                    keycode = e.which;
                                else
                                    return true;
                                if (keycode != 13) {
                                    $(this).trigger('unError');
                                }
                            });
                        }).bind('unError', function() {
                            $(this).removeClass('error');
                        });

                    $('input.validate', retDom).add('textarea.validate', retDom).blur(
                        function(e) {
                            if (!PCT.validate(this)) {
                                PCT.mark(this);
                                e.stopImmediatePropagation();
                            }
                        }).change(function(e) {
                            if (!PCT.validate(this)) {
                                PCT.mark(this);
                                e.stopImmediatePropagation();
                            }
                        });
                    $('input.validate', retDom).add('textarea.validate', retDom).change();
                    return retDom.contents();
                }
            });

        },
        addTemplate:function(template) {
            if (!this.templates) {
                $.extend(this, {
                    templates:new Array()
                })
            }
            this.templates[$(template).attr('id')] = new PCT.template(template);
            $(template).remove();
        },
        getTemplate:function(id) {
            if (this.templates && this.templates[id]) {
                return this.templates[id].getCopy();
            }
        },
        mark : function(item) {
            $(item).trigger('error');
            retValue = false;
        },
        unMark : function(item) {
            $(item).trigger('unError');
        },
        isDate:function  (day, month, year) {
            try {
                day = Number(day);
                month = Number(month);
                year = Number(year);
            } catch(e) {
                return false;
            }
            month--;
            var test = new Date(year, month, day);
            if ((test.getFullYear() == year) &&
                (month == test.getMonth()) &&
                (day == test.getDate()))
                return true;
            else
                return false;
        },
        validate:function(obj) {
            var elem = $(obj);
            PCT.unMark(elem);
            if (elem.hasClass('validate')) {
                var validclass = elem.attr('validclass') ? elem.attr('validclass') : '';

                if (validclass.indexOf('mempty') != -1 && elem.val() == '') {
                    return true;
                }
                if (validclass.indexOf('mempty') == -1 && elem.val() == '') {
                    return false;
                }
                if (elem.val().indexOf('>') != -1 || elem.val().indexOf('<') != -1 || elem.val().indexOf('&') != -1) {
                    return false;
                }
                if (validclass.indexOf('unique') != -1) {
                    var unique = elem.attr('unique');
                    var uniqueroot = elem.attr('uniqueroot');
                    var elVal = elem.val();
                    var counter = 0;
                    $(unique, elem.parents(uniqueroot)).each(function() {
                        if ($(this).val() == elVal) {
                            counter++;
                        }
                    });
                    return counter < 2;
                }
                if (validclass.indexOf('summ') != -1) {
                    if (isNaN(elem.val())) {
                        return false;
                    } else {
                        var minval = elem.attr('minval');
                        var maxval = elem.attr('maxval');
                        var elVal = Number(elem.val());
                        var bl = elVal < 0;
                        if (minval && minval != '' && !isNaN(minval)) {
                            bl = elVal < minval;
                        }
                        if (maxval && maxval != '' && !isNaN(maxval)) {
                            bl = bl || elVal > maxval;
                        }
                        if (bl) {
                            return false;
                        }
                    }
                }
                if (validclass.indexOf('int') != -1) {
                    if (isNaN(elem.val())) {
                        return false;
                    } else {
                        var maxval = elem.attr('maxval');
                        var minval = elem.attr('minval');
                        var elVal = Number(elem.val());
                        var bl = false;
                        if (minval && minval != '' && !isNaN(minval)) {
                            bl = elVal < minval;
                        }
                        if (maxval && maxval != '' && !isNaN(maxval)) {
                            bl = bl || (elVal > maxval);
                        }
                        if (bl) {
                            return false;
                        }
                    }
                }
                if (validclass.indexOf('date') != -1) {
                    var bl = false;
                    var elVal = elem.val();
                    var maxval = elem.datepicker('option', 'maxDate');
                    var minval = elem.datepicker('option', 'minDate');

                    var dateArr = elVal.split('/');
                    if (dateArr.length == 3 && PCT.isDate(dateArr[0], dateArr[1], dateArr[2])) {
                        var dateTest = new Date(dateArr[2], Number(dateArr[1]) - 1, dateArr[0]);
                        if (maxval) {
                            maxval.setHours(0);
                            maxval.setMinutes(0);
                            maxval.setSeconds(0);
                            maxval.setMilliseconds(0);
                            if (dateTest.getTime() > maxval.getTime()) {
                                bl = true;
                            }
                        }
                        if (minval) {
                            minval.setHours(0);
                            minval.setMinutes(0);
                            minval.setSeconds(0);
                            minval.setMilliseconds(0);
                            if (dateTest.getTime() < minval.getTime()) {
                                bl = true;
                            }
                        }
                    } else {
                        bl = true;
                    }
                    if (bl) {
                        return false;
                    }
                }
            }
            return true;
        }
    });
    $.extend(PCT, {
        init:function(templatesContainer, root) {
            PCT.disableAnimation();
            $('template', templatesContainer).each(function() {
                PCT.addTemplate(this);
            });
            {
                var lockDivTemplate = PCT.getTemplate('locker');
                var lockRoot = $('#lockDiv', lockDivTemplate);
                lockRoot.bind("touchmove", PCT.preventDefault);
                lockRoot.bind("touchstart", PCT.preventDefault);
                lockRoot.bind("touchend", PCT.preventDefault);
                lockRoot.bind("click", PCT.preventDefault);
                $.extend(lockDivTemplate, {
                    updateView : function() {
                        var pageHeight = $('body').height();
                        lockRoot.height(pageHeight > window.innerHeight ? pageHeight : innerHeight);
                        $('.animation', lockRoot).css('top', window.pageYOffset + window.innerHeight / 3);
                    }
                });
                root.append(lockDivTemplate);

                $.extend(PCT, {
                    lockDiv : lockDivTemplate
                });

                $(window).bind('scroll', PCT.lockDiv.updateView);
                PCT.updateViewCallbacks.push(PCT.lockDiv.updateView);
            }
            if (window.orientation != undefined) {
                window.onorientationchange = PCT.updateView;
            }
            $(window).resize(PCT.updateView);
            PCT.lockScreen();
            $.ajax({
                url:PCT.location,
                type:'POST',
                data:{
                    action:'getConfig',
                    format:PCT.format
                },
                dataType:PCT.format,
                success:function(data, textStatus, jqXHR) {
                    PCT.tryToInit(data, root);
                },
                error:function(jqXHR, textStatus, errorThrown) {
                    alert('Initialization failed: ' + textStatus);
                },
                statusCode: {
                    551: function() {
                        alert('File reading error.');
                    },
                    552: function() {
                        alert('Config validation error.');
                    },
                    553: function() {
                        alert('Unknown data format.');
                    }
                },
                complete:function() {
                }
            });
        },
        tryToInit:function(initialData, root) {
            $.extend(PCT, {
                currentModel:(new PCT.model()).init($('root', initialData)).setRoot(root)
            });
        },
        sendData:function (url, data, method) {
            if (url && data) {
                var inputs = '';
                $.each(data.split('&'), function() {
                    var pair = this.split('=');
                    inputs += '<input type="hidden" name="' + pair[0] + '" value="' + pair[1] + '" />';
                });
                $('<form action="' + url + '" method="' + (method || 'post') + '">' + inputs + '</form>')
                    .appendTo('body').submit().remove();
            }
        }
    });

    $.extend(PCT, {
        base:{
            setRoot : function(root) {
                if (!root) {
                    root = $('<div></div>');
                }
                $(root).append(this.root.contents());
                this.root = root;
                return this;
            },
            getRoot:function() {
                return this.root;
            }
        }
    });

    $.extend(PCT, {
        model:function(dom) {
            if (!dom) {
                dom = PCT.getTemplate('model');
            }
            dom = $('<div></div>').append(dom);
            $.extend(this, {
                _expiration:$('#Expiration', dom).get(),
                _minBuild:$('#MinBuild', dom).get(),
                _releaseTimestamp:$('#ReleaseTimestamp', dom).get(),
                _products:$('#Products', dom).get(),
                _regions:$('#Regions', dom).get(),
                _services:$('#ModelServices', dom).get(),
                _supportPlans:$('#SupportPlans', dom).get(),
                _authLevels:$('#AuthLevels', dom).get(),
                _currencies:$('#Currencies', dom).get(),
                _recommendations:$('#Recommendations', dom).get(),
                _users:$('#Users', dom).get(),
                _files:$('#Files', dom).get(),
                _addProduct:$('#AddProduct', dom).get(),
                _addRegion:$('#AddRegion', dom).get(),
                _addSupportPlan:$('#AddSupportPlan', dom).get(),
                _addAuthLevel:$('#AddAuthLevel', dom).get(),
                _addServicesGroup:$('#AddServicesGroup', dom).get(),
                _addCurrency:$('#AddCurrency', dom).get(),
                _addRecommendation:$('#AddRecommendation', dom).get(),
                _addUser:$('#AddUser', dom).get(),
                /*_addFile:$('#AddFile', dom).get(),*/
                _home:$('#Home', dom).get(),
                _system:$('#System', dom).get(),
                _reload:$('#Reload', dom).get(),
                _reloadSystem:$('#ReloadSystem', dom).get(),
                _edit:$('#Edit', dom).get(),
                _saveConfiguration:$('#SaveConfiguration', dom).get(),
                _lockConfiguration:$('#LockConfiguration', dom).get(),
                _lockMessage:$('#LockMessage', dom).get(),
                _unlockConfiguration:$('#UnlockConfiguration', dom).get(),
                _core:$('#Core', dom).get(),
                /*_getConfig:$('#Config', dom).get(),*/
                _tabs:new Array()
            });
            var that = this;
            $(this._products, dom).sortable({
                revert:true,
                handle: '.productDrag'
            });
            $(this._regions, dom).sortable({
                revert:true,
                handle: '.regionDrag'
            });
            $(this._supportPlans, dom).sortable({
                revert:true,
                handle: '.supportPlanDrag'
            });
            $(this._authLevels, dom).sortable({
                revert:true,
                handle: '.authLevelDrag'
            });
            $(this._currencies, dom).sortable({
                revert:true,
                handle: '.currencyDrag'
            });
            $(this._recommendations, dom).sortable({
                revert:true,
                handle: '.recommendationDrag'
            });
            $(this._users, dom).sortable({
                revert:true,
                handle: '.userDrag'
            });
            $(this._files, dom).sortable({
                revert:true,
                handle: '.fileDrag'
            });
            $(this._services, dom).sortable({
                revert:true,
                handle: '.groupDrag',
                connectWith: '.divSGroups'
            });
            $('.tab', dom).each(function() {
                that._tabs.push(this);
                var area = $($(this).attr('bind'), dom).get();
                $(this).bind('select', function() {
                    if (!$(this).hasClass('selected')) {
                        for (key in that._tabs) {
                            $(that._tabs[key]).trigger('deselect');
                        }
                        $(this).addClass('selected');
                        $(area).removeClass('hidden');
                    }
                });
                $(this).bind('deselect', function() {
                    $(this).removeClass('selected');
                    $(area).addClass('hidden');
                });
                $(this).click(function() {
                    $(this).trigger('select');
                });
            });
            $(that._tabs[0]).trigger('select');
            $(this._addProduct).click(function() {
                that.addProduct();
            });
            $(this._addRegion).click(function() {
                that.addRegion();
            });
            $(this._addSupportPlan).click(function() {
                that.addSupportPlan();
            });
            $(this._addAuthLevel).click(function() {
                that.addAuthLevel();
            });
            $(this._addServicesGroup).click(function() {
                that.addServicesGroup();
            });
            $(this._addCurrency).click(function() {
                that.addCurrency();
            });
            $(this._addRecommendation).click(function() {
                that.addRecommendation();
            });
            $(this._addUser).click(function() {
                that.addUser();
            });
            $(this._reload).click(function() {
                PCT.getHome(that._home, true);
            });
            $(this._reloadSystem).click(function() {
                PCT.getSystem(that._system, true);
            });
            $(this._edit).click(function() {
                if ($('#Description', that._home).length > 0) {
                    if ($('#Description', that._home).hasClass('hidden')) {
                    } else {
                        $('#DescriptionHtml', that._home).html($('#Description', that._home).val());
                    }
                    $('#Description', that._home).toggleClass('hidden');
                    $('#DescriptionHtml', that._home).toggleClass('hidden');
                }
            });
            /*$(this._addFile).click(function() {
             $('#Uploader input', that._core).click();
             });*/
            new qq.FileUploader({
                element: $('#Uploader', that._core)[0],
                action: PCT.location,
                params: {action:'uploadFile'},
                onSubmit: function(id, fileName) {
                    PCT.lockScreen();
                },
                template:'<div class="titleTableCell titleTableCellButton qq-upload-button">Add File</div><div class="titleTableCell titleTableCellFill"></div><div class="qq-upload-list qq-uploader qq-upload-drop-area hidden"></div>',
                onComplete: function(id, fileName, responseJSON) {
                    PCT.unlockScreen();
                    if (responseJSON && responseJSON.success) {
                        var rt = that.addFile();
                        $('.fileKey', rt).each(function() {
                            $(this).val(fileName).change();
                        });
                    } else {
                        alert('Uploading failed');
                    }
                }
            });
            $.data($(this._core)[0], 'pct', {
                getXML:function(comment) {
                    var config = '<root>';
                    if($(that._releaseTimestamp).val().length > 0){
                        config += '<ReleaseTimestamp>' + $(that._releaseTimestamp).val() + '</ReleaseTimestamp>';
                    }
                    config += '<Expiration>' + $(that._expiration).val() + '</Expiration>';
                    config += '<MinBuild>' + $(that._minBuild).val() + '</MinBuild>';
                    config += '<Comment>' + (comment ? comment : '') + '</Comment>';
                    config += '<Description><![CDATA[' + ($('#Description', that._home).length > 0 ? $('#Description', that._home).val() : '') + ']]></Description>';
                    config += '<Products>';
                    $(that._products).children().each(function() {
                        if ($(this).hasClass('divProduct'))
                            config += $.data($(this)[0], 'pct').getXML();
                    });
                    config += '</Products>';
                    config += '<Regions>';
                    $(that._regions).children().each(function() {
                        if ($(this).hasClass('divRegion'))
                            config += $.data($(this)[0], 'pct').getXML();
                    });
                    config += '</Regions>';
                    config += '<SupportPlans>';
                    $(that._supportPlans).children().each(function() {
                        if ($(this).hasClass('divSupportPlan'))
                            config += $.data($(this)[0], 'pct').getXML();
                    });
                    config += '</SupportPlans>';
                    config += '<AuthLevels>';
                    $(that._authLevels).children().each(function() {
                        if ($(this).hasClass('divAuthLevel'))
                            config += $.data($(this)[0], 'pct').getXML();
                    });
                    config += '</AuthLevels>';
                    config += '<Currencies>';
                    $(that._currencies).children().each(function() {
                        if ($(this).hasClass('divCurrency'))
                            config += $.data($(this)[0], 'pct').getXML();
                    });
                    config += '</Currencies>';
                    config += '<Users>';
                    $(that._users).children().each(function() {
                        if ($(this).hasClass('divUser'))
                            config += $.data($(this)[0], 'pct').getXML();
                    });
                    config += '</Users>';
                    config += '<Files>';
                    $(that._files).children().each(function() {
                        if ($(this).hasClass('divFile'))
                            config += $.data($(this)[0], 'pct').getXML();
                    });
                    config += '</Files>';
                    config += '<Services>';
                    $(that._services).children().each(function() {
                            console.log('pip1');
                        if ($(this).hasClass('divServicesGroup')){
                            console.log('pip2');
                            console.log('pip3='+$.data($(this)[0], 'pct').getXML());
                            config += $.data($(this)[0], 'pct').getXML();
                        }
                    });
                    config += '</Services>';
                    config += '<Recommendations>';
                    $(that._recommendations).children().each(function() {
                        if ($(this).hasClass('divRecommendation'))
                            config += $.data($(this)[0], 'pct').getXML();
                    });
                    config += '</Recommendations>';
                    config += '</root>';
                    console.log(config);
                    return config;
                }
            });
            $.data($(this._products)[0], 'pct', {
                cloneTree:function(xml) {
                    $('root>Product', xml).each(function() {
                        that.addProduct((new PCT.product()).init(this));
                    });
                }
            });
            $.data($(this._regions)[0], 'pct', {
                cloneTree:function(xml) {
                    $('root>Region', xml).each(function() {
                        var rt = that.addRegion((new PCT.region()).setRoot(that._regions).init(this));
                        $('.regionKey', rt).each(function() {
                            $(this).val(PCT.randomString(15)).change();
                        });
                    });
                }
            });
            $.data($(this._supportPlans)[0], 'pct', {
                cloneTree:function(xml) {
                    $('root>SupportPlan', xml).each(function() {
                        var rt = that.addSupportPlan((new PCT.supportPlan()).setRoot(that._supportPlans).init(this));
                        $('.supportPlanKey', rt).each(function() {
                            $(this).val(PCT.randomString(15)).change();
                        });
                    });
                }
            });
            $.data($(this._authLevels)[0], 'pct', {
                cloneTree:function(xml) {
                    $('root>AuthLevel', xml).each(function() {
                        var rt = that.addAuthLevel((new PCT.authLevel()).setRoot(that._authLevels).init(this));
                        $('.authLevelKey', rt).each(function() {
                            $(this).val(PCT.randomString(15)).change();
                        });
                    });
                }
            });
            $.data($(this._services)[0], 'pct', {
                cloneTree:function(xml) {
                    /*$('root>Group', xml).each(function() {
                        that.addServicesGroup((new PCT.servicesGroup()).setRoot(that._services).init(this));
                    });*/
                    $('root>Group', xml).each(function() {
                        var rt = that.addServicesGroup((new PCT.servicesGroup()).setRoot(that._services).init($('>Name', this).text(), $('>ShortName', this).text(), $('>Hint', this).text(), $('>Hidden', this).text(), $('>Key', this).text(), $('>Services', this)));
                        $('.serviceKey', rt).each(function() {
                            $(this).val(PCT.randomString(15)).change();
                        });
                        $('.groupKey', rt).each(function() {
                            $(this).val(PCT.randomString(15)).change();
                        });
                    });
                }
            });
            $.data($(this._currencies)[0], 'pct', {
                cloneTree:function(xml) {
                    $('root>Currency', xml).each(function() {
                        var rt = that.addCurrency((new PCT.currency()).setRoot(that._currencies).init(this));
                        $('.currencyName', rt).each(function() {
                            $(this).val('').change();
                        });
                    });
                }
            });
            $.data($(this._recommendations)[0], 'pct', {
                cloneTree:function(xml) {
                    $('root>Recommendation', xml).each(function() {
                        var rt = that.addRecommendation((new PCT.recommendation()).setRoot(that._recommendations).init(this));
                        $('.recommendationKey', rt).each(function() {
                            $(this).val(PCT.randomString(15)).change();
                        });
                    });
                }
            });
            $.data($(this._users)[0], 'pct', {
                cloneTree:function(xml) {
                    $('root>User', xml).each(function() {
                        var rt = that.addUser((new PCT.user()).setRoot(that._users).init(this));
                        $('.userCN', rt).each(function() {
                            $(this).val('').change();
                        });
                    });
                }
            });
            /*$.data($(this._files)[0], 'pct', {
             cloneTree:function(xml) {
             $('root>File', xml).each(function() {
             that.addFile((new PCT.file()).setRoot(that._files).init(this));
             });
             }
             });*/
            /*$(this._getConfig).click(function() {
             PCT.getConfiguration();
             });*/
            $(this._saveConfiguration).click(function() {
                if ($('.error', that._core).length > 0) {
                    alert('There are some errors in configuration. Fix them first, then try again.');
                    return;
                }
                if (PCT.checker) {
                    PCT.lockScreen();
                    PCT.checker(function() {
                        var message = prompt("Describe configuration changes that you made", "");
                        if (message == null)
                            return;
                        PCT.lockScreen();
                        var config = $.data($(that._core)[0], 'pct').getXML(message);
                        $.ajax({
                            url:PCT.location,
                            type:'POST',
                            data:{
                                action:'saveConfig',
                                config:config
                            },
                            success:function(data, textStatus, jqXHR) {
                                alert('Configuration saved');
                            },
                            error:function(jqXHR, textStatus, errorThrown) {
                                alert('Configuration can\'t be saved: ' + textStatus);
                            },
                            statusCode: {
                                551: function() {
                                    alert('File writing error.');
                                },
                                552: function() {
                                    alert('Config validation error.');
                                }
                            },
                            complete:function() {
                                PCT.unlockScreen();
                            }
                        });
                    }, function(name) {
                        alert("Configuration locked by " + name);
                    }, function() {
                        PCT.unlockScreen();
                    }, function() {
                        alert("Couldn't connect to server. Try again later.");
                    });
                }
            });
            $(this._lockConfiguration).click(function() {
                if (PCT.checker) {
                    PCT.lockScreen();
                    PCT.checker(function() {
                        $.ajax({
                            url:PCT.location,
                            type:'POST',
                            data:{
                                action:'setStatus',
                                locked:true
                            },
                            complete:function() {
                                PCT.checker(function() {
                                }, function() {
                                }, function() {
                                    PCT.unlockScreen();
                                }, function() {
                                });
                            }
                        });
                    }, function(name) {
                        alert("Configuration locked by " + name);
                        PCT.unlockScreen();
                    }, function() {
                    }, function() {
                        alert("Couldn't connect to server. Try again later.");
                        PCT.unlockScreen();
                    });
                }
            });
            $(this._unlockConfiguration).click(function() {
                if (PCT.checker) {
                    PCT.lockScreen();
                    PCT.checker(function() {
                        $.ajax({
                            url:PCT.location,
                            type:'POST',
                            data:{
                                action:'setStatus',
                                locked:false
                            },
                            complete:function() {
                                PCT.checker(function() {
                                }, function() {
                                }, function() {
                                    PCT.unlockScreen();
                                }, function() {
                                });
                            }
                        });
                    }, function(name) {
                        if (confirm("Configuration is locked by " + name + ". Do you really want to unlock configuration?")) {
                            $.ajax({
                                url:PCT.location,
                                type:'POST',
                                data:{
                                    action:'setStatus',
                                    locked:false
                                },
                                complete:function() {
                                    PCT.checker(function() {
                                    }, function() {
                                    }, function() {
                                        PCT.unlockScreen();
                                    }, function() {
                                    });
                                }
                            });
                        } else {
                            PCT.unlockScreen();
                        }
                    }, function() {
                    }, function() {
                        alert("Couldn't connect to server. Try again later.");
                        PCT.unlockScreen();
                    });
                }
            });
            $.extend(this, PCT.base, {
                root:$('<div></div>').append(dom.contents()),
                init:function(initialData) {
                    $(this._expiration).datepicker({
                        dateFormat:'dd/mm/yy',
                        minDate:new Date(),
                        showAnim: PCT.animation,
                        onSelect:function() {
                            PCT.unMark($(this));
                        }
                    });
                    $(this._expiration).val($('>Expiration', initialData).text()).change();
                    $(this._minBuild).val($('>MinBuild', initialData).text()).change();
                    $(this._releaseTimestamp).val($('>ReleaseTimestamp', initialData).text());
                    var that = this;
                    $('>Products>Product', initialData).each(function() {
                        that.addProduct((new PCT.product()).init(this));
                    });
                    $('>Regions>Region', initialData).each(function() {
                        that.addRegion((new PCT.region()).setRoot(that._regions).init(this));
                    });
                    $('>SupportPlans>SupportPlan', initialData).each(function() {
                        that.addSupportPlan((new PCT.supportPlan()).setRoot(that._supportPlans).init(this));
                    });
                    $('>AuthLevels>AuthLevel', initialData).each(function() {
                        that.addAuthLevel((new PCT.authLevel()).setRoot(that._authLevels).init(this));
                    });
                    $('>Services>Group', initialData).each(function() {
                        that.addServicesGroup((new PCT.servicesGroup()).setRoot(that._services).init($('>Name', this).text(), $('>ShortName', this).text(), $('>Hint', this).text(), $('>Hidden', this).text(), $('>Key', this).text(), $('>Services', this)));
                    });
                    $('>Currencies>Currency', initialData).each(function() {
                        that.addCurrency((new PCT.currency()).setRoot(that._currencies).init(this));
                    });
                    $('>Recommendations>Recommendation', initialData).each(function() {
                        that.addRecommendation((new PCT.recommendation()).setRoot(that._recommendations).init(this));
                    });
                    $('>Users>User', initialData).each(function() {
                        that.addUser((new PCT.user()).setRoot(that._users).init(this));
                    });
                    $('>Files>File', initialData).each(function() {
                        that.addFile((new PCT.file()).setRoot(that._files).init(this));
                    });
                    PCT.unlockScreen();
                    PCT.getHome(that._home, true, function() {
                        PCT.getSystem(that._system, true);
                    });

                    if (!PCT.checker) {
                        $.extend(PCT, {
                            checker:function(successCallback, failCallback, complete, error) {
                                PCT.checkStatus(function() {
                                    $(that._saveConfiguration).removeClass('hidden');
                                    $(that._lockConfiguration).addClass('hidden');
                                    $(that._unlockConfiguration).removeClass('hidden').addClass('lockedByYou').removeClass('locked');
                                    $(that._lockMessage).html('Configuration is locked by you');
                                    $(that._lockMessage).addClass('lockedByYou').removeClass('locked');
                                    if (successCallback)successCallback();
                                }, function(name) {
                                    $(that._saveConfiguration).addClass('hidden');
                                    $(that._lockConfiguration).addClass('hidden');
                                    $(that._unlockConfiguration).removeClass('hidden').addClass('locked').removeClass('lockedByYou');
                                    $(that._lockMessage).html('Configuration is locked by ' + name);
                                    $(that._lockMessage).addClass('locked').removeClass('lockedByYou');
                                    if (failCallback)failCallback(name);
                                }, function() {
                                    $(that._saveConfiguration).removeClass('hidden');
                                    $(that._lockConfiguration).removeClass('hidden');
                                    $(that._unlockConfiguration).addClass('hidden');
                                    $(that._lockMessage).empty();
                                    $(that._lockMessage).removeClass('locked').removeClass('lockedByYou');
                                    if (successCallback)successCallback();
                                }, function() {
                                    if (!successCallback && !failCallback && !complete && !error)window.setTimeout("PCT.checker()", PCT.timer);
                                    if (complete)complete();
                                }, function() {
                                    if (error)error();
                                });
                            }
                        });
                    }
                    PCT.checker();
                    return this;
                },
                addProduct:function(product) {
                    if (product) {
                        product.setRoot(this._products);
                        $('html, body').animate({
                            scrollTop: $(product.getHead()).offset().top
                        }, 000);
                    } else {
                        this.addProduct((new PCT.product()).addCapacitiesRoot().addModulesRoot());
                    }
                },
                addRegion:function(region) {
                    if (region) {
                        $('html, body').animate({
                            scrollTop: $(region.getHead()).offset().top
                        }, 200);
                        return region.getHead();
                    } else {
                        return this.addRegion((new PCT.region()).setRoot(that._regions));
                    }
                },
                addSupportPlan:function(supportPlan) {
                    if (supportPlan) {
                        $('html, body').animate({
                            scrollTop: $(supportPlan.getHead()).offset().top
                        }, 200);
                        return supportPlan.getHead();
                    } else {
                        return this.addSupportPlan((new PCT.supportPlan()).setRoot(that._supportPlans));
                    }
                },
                addAuthLevel:function(authLevel) {
                    if (authLevel) {
                        $('html, body').animate({
                            scrollTop: $(authLevel.getHead()).offset().top
                        }, 200);
                        return authLevel.getHead();
                    } else {
                        return this.addAuthLevel((new PCT.authLevel()).setRoot(that._authLevels));
                    }
                },
                addServicesGroup:function(servicesGroup) {
                    if (servicesGroup) {
                        $('html, body').animate({
                            scrollTop: $(servicesGroup.getHead()).offset().top
                        }, 200);
                        return servicesGroup.getHead();
                    } else {
                        return this.addServicesGroup((new PCT.servicesGroup()).setRoot(that._services));
                    }
                },
                addCurrency:function(currency) {
                    if (currency) {
                        $('html, body').animate({
                            scrollTop: $(currency.getHead()).offset().top
                        }, 200);
                        return currency.getHead();
                    } else {
                        return this.addCurrency((new PCT.currency()).setRoot(that._currencies));
                    }
                },
                addRecommendation:function(recommendation) {
                    if (recommendation) {
                        $('html, body').animate({
                            scrollTop: $(recommendation.getHead()).offset().top
                        }, 200);
                        return recommendation.getHead();
                    } else {
                        return this.addRecommendation((new PCT.recommendation()).setRoot(that._recommendations));
                    }
                },
                addUser:function(user) {
                    if (user) {
                        $('html, body').animate({
                            scrollTop: $(user.getHead()).offset().top
                        }, 200);
                        return user.getHead();
                    } else {
                        return this.addUser((new PCT.user()).setRoot(that._users));
                    }
                },
                addFile:function(file) {
                    if (file) {
                        $('html, body').animate({
                            scrollTop: $(file.getHead()).offset().top
                        }, 200);
                        return file.getHead();
                    } else {
                        return this.addFile((new PCT.file()).setRoot(that._files));
                    }
                }
            });
        },
        product:function(dom) {
            if (!dom) {
                dom = PCT.getTemplate('product');
            }
            dom = $('<div></div>').append(dom);
            $.extend(this, {
                _totalWeightInt:0,
                _head:$(dom).children().first().get(),
                _body:$(dom).contents(),
                _name:$('#Name', dom).get(),
                _shortName:$('#ShortName', dom).get(),
                _maximumFunctionalityPrice:$('#MaximumFunctionalityPrice', dom).get(),
                _minimumPrice:$('#MinimumPrice', dom).get(),
                _content:$('#Content', dom).get(),
                _modules:$('#Modules', dom).get(),
                _licenses:$('#Licenses', dom).get(),
                _addLicense:$('#AddLicense', dom).get(),
                _capacities:$('#Capacities', dom).get(),
                _productTitle:$('#ProductTitle', dom).get(),
                _settings:$('#Settings', dom).get(),
                _settingsPane:$('#SettingsPane', dom).get(),
                _expand:$('#Expand', dom).get(),
                _remove:$('#Remove', dom).get(),
                _secondarySalesRate:$('#SecondarySalesRate', dom).get(),
                _clone:$('#Clone', dom).get(),
                _totalWeight:$('#TotalWeight', dom).get(),
                _pricePerOne:$('#PricePerOne', dom).get(),
                //_hint:$('#Hint', dom).get(),
                _core:$('#Core', dom).get()
            });
            var that = this;
            $.data($(this._core)[0], 'pct', {
                getXML:function() {
                    var config = '<Product>';
                    config += '<Name>' + $(that._name).val() + '</Name>';
                    //config += '<Hint>' + $(that._hint).val() + '</Hint>';
                    config += '<ShortName>' + $(that._shortName).val() + '</ShortName>';
                    config += '<SecondarySalesRate>' + $(that._secondarySalesRate).val() + '</SecondarySalesRate>';
                    config += '<MaximumFunctionalityPrice>' + $(that._maximumFunctionalityPrice).val() + '</MaximumFunctionalityPrice>';
                    config += '<MinimumPrice>' + $(that._minimumPrice).val() + '</MinimumPrice>';
                    config += '<Licenses>';
                    $(that._licenses).children().each(function() {
                        if ($(this).hasClass('divLicense'))
                            config += $.data($(this)[0], 'pct').getXML();
                    });
                    config += '</Licenses>';
                    $(that._modules).children().each(function() {
                        if ($(this).hasClass('divModulesGroup'))
                            config += $.data($(this)[0], 'pct').getXML();
                    });
                    $(that._capacities).children().each(function() {
                        if ($(this).hasClass('divCapacitiesGroup'))
                            config += $.data($(this)[0], 'pct').getXML();
                    });
                    config += '</Product>';
                    return config;
                }
            });
            $(this._licenses, dom).sortable({
                revert:true,
                handle: '.licenseDrag',
                connectWith: '.divProductLicenses'
            });
            $.data($(this._modules)[0], 'pct', {
                updateTotal:function() {
                    that.updateTotal();
                }
            });
            $.data($(this._licenses)[0], 'pct', {
                cloneTree:function(xml) {
                    $('root>License', xml).each(function() {
                        var rt = that.addLicense((new PCT.license()).setRoot(that._licenses).init(this));
                        $('.licenseKey', rt).each(function() {
                            $(this).val(PCT.randomString(15)).change();
                        });
                    });
                }
            });
            $(this._addLicense).click(function() {
                if ($(that._content).hasClass('hidden')) {
                    $(that._content).removeClass('hidden');
                    $(that._expand).html('Collapse');
                }
                that.addLicense();
            });
            $(this._remove).click(function() {
                if (confirm('Remove product?')) {
                    if (confirm('Removing product can result in broken backward compatibility. Remove product?')) {
                        $(that._body).remove();
                    }
                }
            });
            $(this._clone).click(function() {
                $.data($(that._core).parents('.divModelProducts')[0], 'pct').cloneTree($.parseXML('<root>' + $.data($(that._core)[0], 'pct').getXML() + '</root>'));
            });
            $(this._expand).click(function(arg) {
                $(that._content).toggleClass('hidden');
                if ($(that._content).hasClass('hidden')) {
                    $(that._expand).html('Expand');
                } else {
                    $(that._expand).html('Collapse');
                }
            });

            $(this._name).change(function() {
                $(that._productTitle).html($(this).val());
            });
            $(this._productTitle).click(function() {
                $(that._remove).toggleClass('hidden');
                $(that._settingsPane).toggleClass('hidden');
            });
            $(this._maximumFunctionalityPrice).change(function() {
                if (!$(that._maximumFunctionalityPrice).hasClass('error') && that._totalWeightInt > 0) {
                    try {
                        $(that._pricePerOne).html((Number($(that._maximumFunctionalityPrice).val()) / that._totalWeightInt).toFixed(2));
                    } catch(e) {
                        $(that._pricePerOne).empty();
                    }
                } else {
                    $(that._pricePerOne).empty();
                }
            });
            $.extend(this, PCT.base, {
                root:$('<div></div>').append(dom.contents()),
                getHead:function() {
                    return this._head;
                },
                updateTotal:function() {
                    var totalWeight = 0;
                    $('.moduleWeight', that._modules).each(function() {
                        if (!$(this).hasClass('error')) {
                            totalWeight += Number($(this).val());
                        }
                    });
                    $(that._totalWeight).html(totalWeight);
                    that._totalWeightInt = totalWeight;
                    $(that._maximumFunctionalityPrice).change();
                },
                init:function(initialData) {
                    $(this._name).val($('>Name', initialData).text()).change();
                    //$(this._hint).val($('>Hint', initialData).text()).change();
                    $(this._shortName).val($('>ShortName', initialData).text()).change();
                    $('>Licenses>License', initialData).each(function() {
                        that.addLicense((new PCT.license()).setRoot(that._licenses).init(this));
                    });
                    $(this._maximumFunctionalityPrice).val($('>MaximumFunctionalityPrice', initialData).text()).change();
                    $(this._minimumPrice).val($('>MinimumPrice', initialData).text()).change();
                    $(this._secondarySalesRate).val($('>SecondarySalesRate', initialData).text()).change();
                    this.addModulesRoot((new PCT.modulesGroup()).setRoot(this._modules).init(null, null, null, null, null, null, $('>Modules', initialData)));
                    this.addCapacitiesRoot((new PCT.capacitiesGroup()).setRoot(this._capacities).init(null, null, null, null, $('>Capacities', initialData)));
                    $(this._settingsPane).addClass('hidden');
                    $(this._content).addClass('hidden');
                    $(this._remove).addClass('hidden');
                    $(this._expand).html('Expand');
                    this.updateTotal();
                    return this;
                },
                addModulesRoot:function(modulesGroup) {
                    if (modulesGroup) {
                        modulesGroup.setIsRoot(true);
                    } else {
                        this.addModulesRoot((new PCT.modulesGroup()).setIsRoot(true).setRoot(this._modules));
                    }
                    return this;
                },
                addLicense:function(license) {
                    if (license) {
                        $('html, body').animate({
                            scrollTop: $(license.getHead()).offset().top
                        }, 200);
                        return license.getHead();
                    } else {
                        return this.addLicense((new PCT.license()).setRoot(that._licenses));
                    }
                },
                addCapacitiesRoot:function(capacitiesGroup) {
                    if (capacitiesGroup) {
                        capacitiesGroup.setIsRoot(true);
                    } else {
                        this.addCapacitiesRoot((new PCT.capacitiesGroup()).setIsRoot(true).setRoot(this._capacities));
                    }
                    return this;
                }
            });
        },
        modulesGroup:function(dom) {
            if (!dom) {
                dom = PCT.getTemplate('modulesGroup');
            }
            dom = $('<div></div>').append(dom);
            $.extend(this, {
                _head:$(dom).children().first().get(),
                _body:$(dom).contents(),
                _name:$('#Name', dom).get(),
                _shortName:$('#ShortName', dom).get(),
                _content:$('#Content', dom).get(),
                _modules:$('#Modules', dom).get(),
                _groups:$('#Groups', dom).get(),
                _isRoot:$('#IsRoot', dom).get(),
                _groupTitle:$('#GroupTitle', dom).get(),
                _settings:$('#Settings', dom).get(),
                _settingsPane:$('#SettingsPane', dom).get(),
                _addGroup:$('#AddGroup', dom).get(),
                _addModule:$('#AddModule', dom).get(),
                _expand:$('#Expand', dom).get(),
                _hidden:$('#Hidden', dom).get(),
                _remove:$('#Remove', dom).get(),
                _core:$('#Core', dom).get(),
                _clone:$('#Clone', dom).get(),
                _hint:$('#Hint', dom).get(),
                _radioButtonGroup:$('#RadioButtonGroup', dom).get(),
                _defaultModuleKey:$('#DefaultModuleKey', dom).get(),
                _drag:$('#Drag', dom).get()
            });
            var that = this;
            $(this._modules, dom).sortable({
                revert:true,
                handle: '.moduleDrag',
                connectWith: '.divModules',
                start:function() {
                    that.updateTotal();
                },
                update:function() {
                    that.updateTotal();
                }
            });
            $(this._radioButtonGroup).change(
                function() {
                    if ($(this).prop('checked')) {
                        $(that._defaultModuleKey).addClass('validate').change();
                        $(that._defaultModuleKey).parents('.settingsTableRow').removeClass('hidden');
                    } else {
                        $(that._defaultModuleKey).removeClass('validate').removeClass('error').change();
                        $(that._defaultModuleKey).parents('.settingsTableRow').addClass('hidden');
                    }
                }).change();
            $(this._groups, dom).sortable({
                revert:true,
                handle: '.groupDrag',
                connectWith: '.divMGroups',
                start:function() {
                    that.updateTotal();
                },
                update:function() {
                    that.updateTotal();
                }
            });
            $.data($(this._core)[0], 'pct', {
                getXML:function() {
                    var config = '';
                    if (!that.getIsRoot()) {
                        config += '<Group>';
                        config += '<Name>' + $(that._name).val() + '</Name>';
                        config += '<ShortName>' + $(that._shortName).val() + '</ShortName>';
                        config += '<Hidden>' + ($(that._hidden).prop('checked') ? 'true' : 'false') + '</Hidden>';
                        config += '<Hint>' + $(that._hint).val() + '</Hint>';
                        config += '<DefaultModuleKey>' + $(that._defaultModuleKey).val() + '</DefaultModuleKey>';
                        config += '<RadioButtonGroup>' + ($(that._radioButtonGroup).prop('checked') ? 'true' : 'false') + '</RadioButtonGroup>';
                    }
                    config += '<Modules>';
                    $(that._modules).children().each(function() {
                        if ($(this).hasClass('divModule'))
                            config += $.data($(this)[0], 'pct').getXML();
                    });
                    $(that._groups).children().each(function() {
                        if ($(this).hasClass('divModulesGroup'))
                            config += $.data($(this)[0], 'pct').getXML();
                    });
                    config += '</Modules>';
                    if (!that.getIsRoot()) {
                        config += '</Group>';
                    }
                    return config;
                }
            });
            $.data($(this._groups)[0], 'pct', {
                cloneTree:function(xml) {
                    $('root>Group', xml).each(function() {
                        var rt = that.addGroup((new PCT.modulesGroup()).setRoot(that._groups).init($('>Name', this).text(), $('>ShortName', this).text(), $('>Hint', this).text(), $('>RadioButtonGroup', this).text(), $('>Hidden', this).text(), $('>DefaultModuleKey', this).text(), $('>Modules', this)).setIsRoot(false));
                        $('.moduleKey', rt).each(function() {
                            $(this).val(PCT.randomString(15)).change();
                        });
                    });
                    that.updateTotal();
                }
            });
            $.data($(this._modules)[0], 'pct', {
                cloneTree:function(xml) {
                    $('root>Module', xml).each(function() {
                        var rt = that.addModule((new PCT.module()).setRoot(that._modules).init(this));
                        $('.moduleKey', rt).each(function() {
                            $(this).val(PCT.randomString(15)).change();
                        });
                    });
                    that.updateTotal();
                }
            });
            $(this._remove).click(function() {
                if (confirm('Remove group?')) {
                    if (confirm('Group will be removed with nested modules. Removing module can result in broken backward compatibility. Remove group?')) {
                        $('.moduleWeight', that._content).val(0);
                        that.updateTotal();
                        $(that._body).remove();
                    }
                }
            });
            $(this._clone).click(function() {
                $.data($(that._core).parents('.divGroups')[0], 'pct').cloneTree($.parseXML('<root>' + $.data($(that._core)[0], 'pct').getXML() + '</root>'));
            });
            $(this._expand).click(function(arg) {
                $(that._content).toggleClass('hidden');
                if ($(that._content).hasClass('hidden')) {
                    $(that._expand).html('Expand');
                } else {
                    $(that._expand).html('Collapse');
                }
            });
            $(this._addGroup).click(function() {
                if ($(that._content).hasClass('hidden')) {
                    $(that._content).removeClass('hidden');
                    $(that._expand).html('Collapse');
                }
                that.addGroup();
            });
            $(this._addModule).click(function() {
                if ($(that._content).hasClass('hidden')) {
                    $(that._content).removeClass('hidden');
                    $(that._expand).html('Collapse');
                }
                that.addModule();
            });
            $(this._name).change(function() {
                $(that._groupTitle).html($(this).val());
            });
            $(this._isRoot).change(function() {
                if ($(this).val() == 'true') {
                    $(that._name).val('MR').change();
                    $(that._drag).addClass('hidden');
                    $(that._clone).addClass('hidden');
                    $(that._content).addClass('hidden');
                    $(that._expand).html('Expand');
                    $(that._settings).addClass('hidden');
                    $(that._groupTitle).html('Modules Root');
                    $(that._settingsPane).addClass('hidden');
                    $(that._remove).addClass('hidden');
                    $(that._groupTitle).addClass('unclickable');
                } else {
                    $(that._groupTitle).click(function() {
                        $(that._remove).toggleClass('hidden');
                        $(that._settingsPane).toggleClass('hidden');
                    });
                }
            });
            $.extend(this, PCT.base, {
                root:$('<div></div>').append(dom.contents()),
                getHead:function() {
                    return this._head;
                },
                setIsRoot:function(r) {
                    $(this._isRoot).val(r ? 'true' : false).change();
                    return this;
                },
                getIsRoot:function() {
                    return $(this._isRoot).val() == 'true';
                },
                updateTotal:function() {
                    $.data($(that._core).parents('.divProductModules')[0], 'pct').updateTotal();
                },
                init:function(name, shortName, hint, radioButtonGroup, hidden, defaultModuleKey, initialData) {
                    if (name) {
                        $(this._name).val(name).change();
                    }
                    var that = this;
                    if (shortName) {
                        $(this._shortName).val(shortName).change();
                    }
                    if (radioButtonGroup) {
                        $(this._radioButtonGroup).prop('checked', (radioButtonGroup == 'true')).change();
                    }
                    if (hidden) {
                        $(this._hidden).prop('checked', (hidden == 'true')).change();
                    }
                    if (defaultModuleKey) {
                        $(this._defaultModuleKey).val(defaultModuleKey).change();
                    }
                    if (hint) {
                        $(this._hint).val(hint).change();
                    }
                    $('>Module', initialData).each(function() {
                        that.addModule((new PCT.module()).setRoot(that._modules).init(this));
                    });
                    $('>Group', initialData).each(function() {
                        that.addGroup((new PCT.modulesGroup()).setRoot(that._groups).init($('>Name', this).text(), $('>ShortName', this).text(), $('>Hint', this).text(), $('>RadioButtonGroup', this).text(), $('>Hidden', this).text(), $('>DefaultModuleKey', this).text(), $('>Modules', this)).setIsRoot(false));
                    });
                    $(this._settingsPane).addClass('hidden');
                    $(this._remove).addClass('hidden');
                    return this;
                },
                addModule:function(module) {
                    if (module) {
                        $('html, body').animate({
                            scrollTop: $(module.getHead()).offset().top
                        }, 000);
                        return module.getHead();
                    } else {
                        return this.addModule((new PCT.module()).setRoot(this._modules));
                    }
                },
                addGroup:function(group) {
                    if (group) {
                        $('html, body').animate({
                            scrollTop: $(group.getHead()).offset().top
                        }, 000);
                        return group.getHead();
                    } else {
                        return this.addGroup((new PCT.modulesGroup()).setRoot(this._groups).setIsRoot(false));
                    }
                }
            });
        },
        module:function(dom) {
            if (!dom) {
                dom = PCT.getTemplate('module');
            }
            dom = $('<div></div>').append(dom);
            $.extend(this, {
                _allowWeightRecalc:true,
                _head:$(dom).children().first().get(),
                _body:$(dom).contents(),
                _name:$('#Name', dom).get(),
                _shortName:$('#ShortName', dom).get(),
                _weight:$('#Weight', dom).get(),
                _key:$('#Key', dom).get(),
                _secondarySalesPrice:$('#SecondarySalesPrice', dom).get(),
                _secondarySalesRate:$('#SecondarySalesRate', dom).get(),
                _deprecated:$('#Deprecated', dom).get(),
                _hidden:$('#Hidden', dom).get(),
                _moduleTitle:$('#Title', dom).get(),
                _settings:$('#Settings', dom).get(),
                _dependencies:$('#Dependencies', dom).get(),
                _addDependency:$('#AddDependency', dom).get(),
                _settingsPane:$('#SettingsPane', dom).get(),
                _expand:$('#Expand', dom).get(),
                _remove:$('#Remove', dom).get(),
                _clone:$('#Clone', dom).get(),
                _hint:$('#Hint', dom).get(),
                _core:$('#Core', dom).get()
            });
            var that = this;
            $(this._dependencies, dom).sortable({
                revert:true,
                handle: '.dependencyDrag',
                connectWith: '.divDependencies'
            });
            $(this._clone).click(function() {
                $.data($(that._core).parents('.divModules')[0], 'pct').cloneTree($.parseXML('<root>' + $.data($(that._core)[0], 'pct').getXML() + '</root>'));
            });
            $.data($(this._core)[0], 'pct', {
                getXML:function() {
                    var config = '<Module>';
                    config += '<Name>' + $(that._name).val() + '</Name>';
                    config += '<Hint>' + $(that._hint).val() + '</Hint>';
                    config += '<ShortName>' + $(that._shortName).val() + '</ShortName>';
                    config += '<Weight>' + $(that._weight).val() + '</Weight>';
                    config += '<Key>' + $(that._key).val() + '</Key>';
                    config += '<SecondarySales>';
                    config += '<Price>' + $(that._secondarySalesPrice).val() + '</Price>';
                    config += '<Rate>' + $(that._secondarySalesRate).val() + '</Rate>';
                    config += '</SecondarySales>';
                    config += '<Deprecated>' + ($(that._deprecated).prop('checked') ? 'true' : 'false') + '</Deprecated>';
                    config += '<Hidden>' + ($(that._hidden).prop('checked') ? 'true' : 'false') + '</Hidden>';
                    config += '<Dependencies>';
                    $(that._dependencies).children().each(function() {
                        if ($(this).hasClass('requireDependency'))
                            config += '<Require>' + $.data($(this)[0], 'pct').getXML() + '</Require>';
                    });
                    $(that._dependencies).children().each(function() {
                        if ($(this).hasClass('excludeDependency'))
                            config += '<Exclude>' + $.data($(this)[0], 'pct').getXML() + '</Exclude>';
                    });
                    $(that._dependencies).children().each(function() {
                        if ($(this).hasClass('requireCapacityDependency'))
                            config += '<RequireCapacity' + $.data($(this)[0], 'pct').getAttributesString() + '>' + $.data($(this)[0], 'pct').getXML() + '</RequireCapacity>';
                    });
                    config += '</Dependencies>';
                    config += '</Module>';
                    return config;
                }
            });
            $(this._remove).click(function() {
                if (confirm('Remove module?')) {
                    if (confirm('Removing module can result in broken backward compatibility. Remove module?')) {
                        $(that._weight).val(0).change();
                        $(that._body).remove();
                    }
                }
            });
            $(this._name).change(function() {
                $(that._moduleTitle).html($(this).val());
            });
            $(this._addDependency).click(function() {
                if ($(that._dependencies).hasClass('hidden')) {
                    $(that._dependencies).removeClass('hidden');
                    $(that._expand).html('Collapse');
                }
                that.addDependency();
            });
            $(this._deprecated).change(function() {
                if ($(this).prop('checked')) {
                    $(that._moduleTitle).addClass('deprecated');
                } else {
                    $(that._moduleTitle).removeClass('deprecated');
                }
            });
            $(this._key).val(PCT.randomString(15)).change();
            $(this._moduleTitle).click(function() {
                $(that._remove).toggleClass('hidden');
                $(that._settingsPane).toggleClass('hidden');
            });
            $(this._expand).click(function(arg) {
                $(that._dependencies).toggleClass('hidden');
                if ($(that._dependencies).hasClass('hidden')) {
                    $(that._expand).html('Expand');
                } else {
                    $(that._expand).html('Collapse');
                }
            });
            $(this._weight).change(function() {
                if (that._allowWeightRecalc) {
                    that.updateTotal();
                }
            });
            $.extend(this, PCT.base, {
                root:$('<div></div>').append(dom.contents()),
                getHead:function() {
                    return this._head;
                },
                updateTotal:function() {
                    $.data($(that._core).parents('.divProductModules')[0], 'pct').updateTotal();
                },
                init:function(initialData) {
                    $(this._name).val($('>Name', initialData).text()).change();
                    $(this._hint).val($('>Hint', initialData).text()).change();
                    $(this._shortName).val($('>ShortName', initialData).text()).change();
                    this._allowWeightRecalc = false;
                    $(this._weight).val($('>Weight', initialData).text()).change();
                    this._allowWeightRecalc = true;
                    $(this._key).val('').val($('>Key', initialData).text()).change();
                    $(this._secondarySalesPrice).val($('>SecondarySales>Price', initialData).text()).change();
                    $(this._secondarySalesRate).val($('>SecondarySales>Rate', initialData).text()).change();
                    $(this._deprecated).prop('checked', ($('>Deprecated', initialData).text() == 'true')).change();
                    $(this._hidden).prop('checked', ($('>Hidden', initialData).text() == 'true')).change();
                    $(this._settingsPane).addClass('hidden');
                    $(this._dependencies).addClass('hidden');
                    $(this._remove).addClass('hidden');
                    $(this._expand).html('Expand');
                    var that = this;
                    $('>Dependencies>Require', initialData).each(function() {
                        that.addDependency((new PCT.dependency()).setRoot(that._dependencies).setType('require').init($(this).text()));
                    });
                    $('>Dependencies>Exclude', initialData).each(function() {
                        that.addDependency((new PCT.dependency()).setRoot(that._dependencies).setType('exclude').init($(this).text()));
                    });
                    $('>Dependencies>RequireCapacity', initialData).each(function() {
                        that.addDependency((new PCT.dependency()).setRoot(that._dependencies).setType('capacity').setValue($(this).attr('value')).setIncremental($(this).attr('incremental') == 'true').setFreeOfCharge($(this).attr('freeofcharge') == 'true').init($(this).text()));
                    });
                    return this;
                },
                addDependency:function(dependency) {
                    if (dependency) {
                        $('html, body').animate({
                            scrollTop: $(dependency.getHead()).offset().top
                        }, 000);
                    } else {
                        this.addDependency((new PCT.dependency()).setRoot(this._dependencies).setType('require'));
                    }
                }
            });
        },
        dependency:function(dom) {
            if (!dom) {
                dom = PCT.getTemplate('dependency');
            }
            dom = $('<div></div>').append(dom);
            $.extend(this, {
                _head:$(dom).children().first().get(),
                _body:$(dom).contents(),
                _key:$('#Key', dom).get(),
                _value:$('#Value', dom).get(),
                _incremental:$('#Incremental', dom).get(),
                _freeOfCharge:$('#FreeOfCharge', dom).get(),
                _dependencyBody:$('#DependencyBody', dom).get(),
                _dependencyTitle:$('#Title', dom).get(),
                _settings:$('#Settings', dom).get(),
                _type:$('#Type', dom).get(),
                _settingsPane:$('#SettingsPane', dom).get(),
                _remove:$('#Remove', dom).get(),
                _core:$('#DependencyBody', dom).get(),
                _keyTitle:$('#KeyTitle', dom).get()
            });
            var that = this;
            $.data($(this._core)[0], 'pct', {
                getXML:function() {
                    return $(that._key).val();
                },
                getAttributesString:function() {
                    var rets = '';
                    rets += ' value="' + $(that._value).val() + '"';
                    if ($(that._incremental).prop('checked')) {
                        rets += ' incremental="true"';
                        if ($(that._freeOfCharge).prop('checked')) {
                            rets += ' freeofcharge="true"';
                        }
                    }
                    return rets;
                }
            });
            $(this._incremental).change(function() {
                if ($(this).prop('checked')) {
                    $(that._dependencyBody).addClass('incremental');
                } else {
                    $(that._dependencyBody).removeClass('incremental');
                }
            });
            $(this._remove).click(
                function() {
                    if (confirm('Remove dependency?')) {
                        $(that._body).remove();
                    }
                });
            $(this._key).change(function() {
                $(that._dependencyTitle).html($(this).val());
            });
            $(this._type).change(function() {
                if ($(this).val() == 'require') {
                    $(that._dependencyBody).addClass('requireDependency');
                    $(that._dependencyBody).removeClass('excludeDependency').removeClass('requireCapacityDependency');
                    $(that._keyTitle).html('Key(s):');
                    $(that._value).removeClass('validate').change();
                } else if ($(this).val() == 'exclude') {
                    $(that._dependencyBody).addClass('excludeDependency');
                    $(that._dependencyBody).removeClass('requireDependency').removeClass('requireCapacityDependency');
                    $(that._keyTitle).html('Key:');
                    $(that._value).removeClass('validate').change();
                } else if ($(this).val() == 'capacity') {
                    $(that._dependencyBody).addClass('requireCapacityDependency');
                    $(that._keyTitle).html('Key:');
                    $(that._value).addClass('validate').change();
                    $(that._dependencyBody).removeClass('requireDependency').removeClass('excludeDependency');
                }
            });
            $(this._dependencyTitle).click(
                function() {
                    $(that._remove).toggleClass('hidden');
                    $(that._settingsPane).toggleClass('hidden');
                });
            $.extend(this, PCT.base, {
                root:$('<div></div>').append(dom.contents()),
                getHead:function() {
                    return this._head;
                },
                init:function(initialData) {
                    $(this._key).val(initialData).change();
                    $(this._settingsPane).addClass('hidden');
                    $(this._remove).addClass('hidden');
                    return this;
                },
                setType:function(type) {
                    $(this._type).val(type).change();
                    return this;
                },
                setValue:function(value) {
                    $(this._value).val(value).change();
                    return this;
                },
                setIncremental:function(incremental) {
                    $(this._incremental).prop('checked', incremental).change();
                    return this;
                },
                setFreeOfCharge:function(freeOfCharge) {
                    $(this._freeOfCharge).prop('checked', freeOfCharge).change();
                    return this;
                }
            });
        },
        capacitiesGroup:function(dom) {
            if (!dom) {
                dom = PCT.getTemplate('capacitiesGroup');
            }
            dom = $('<div></div>').append(dom);
            $.extend(this, {
                _head:$(dom).children().first().get(),
                _body:$(dom).contents(),
                _name:$('#Name', dom).get(),
                _shortName:$('#ShortName', dom).get(),
                _content:$('#Content', dom).get(),
                _capacities:$('#Capacities', dom).get(),
                _groups:$('#Groups', dom).get(),
                _isRoot:$('#IsRoot', dom).get(),
                _groupTitle:$('#GroupTitle', dom).get(),
                _addGroup:$('#AddGroup', dom).get(),
                _addCapacity:$('#AddCapacity', dom).get(),
                _addLink:$('#AddLink', dom).get(),
                _settings:$('#Settings', dom).get(),
                _settingsPane:$('#SettingsPane', dom).get(),
                _expand:$('#Expand', dom).get(),
                _remove:$('#Remove', dom).get(),
                _hidden:$('#Hidden', dom).get(),
                _core:$('#Core', dom).get(),
                _clone:$('#Clone', dom).get(),
                _hint:$('#Hint', dom).get(),
                _drag:$('#Drag', dom).get()
            });
            var that = this;
            $(this._capacities, dom).sortable({
                revert:true,
                handle: '.capacityDrag',
                connectWith: '.divCapacities'
            });
            $(this._groups, dom).sortable({
                revert:true,
                handle: '.groupDrag',
                connectWith: '.divCGroups'
            });
            $.data($(this._core)[0], 'pct', {
                getXML:function() {
                    var config = '';
                    if (!that.getIsRoot()) {
                        config += '<Group>';
                        config += '<Name>' + $(that._name).val() + '</Name>';
                        config += '<ShortName>' + $(that._shortName).val() + '</ShortName>';
                        config += '<Hint>' + $(that._hint).val() + '</Hint>';
                        config += '<Hidden>' + ($(that._hidden).prop('checked') ? 'true' : 'false') + '</Hidden>';
                    }
                    config += '<Capacities>';
                    $(that._capacities).children().each(function() {
                        if ($(this).hasClass('divCapacity'))
                            config += $.data($(this)[0], 'pct').getXML();
                    });
                    $(that._groups).children().each(function() {
                        if ($(this).hasClass('divCapacitiesGroup'))
                            config += $.data($(this)[0], 'pct').getXML();
                    });
                    config += '</Capacities>';
                    if (!that.getIsRoot()) {
                        config += '</Group>';
                    }
                    return config;
                }
            });
            $.data($(this._groups)[0], 'pct', {
                cloneTree:function(xml) {
                    $('root>Group', xml).each(function() {
                        var rt = that.addGroup((new PCT.capacitiesGroup()).setRoot(that._groups).init($('>Name', this).text(), $('>ShortName', this).text(), $('>Hint', this).text(), $('>Hidden', this).text(), $('>Capacities', this)).setIsRoot(false));
                        $('.capacityKey', rt).each(function() {
                            $(this).val(PCT.randomString(15)).change();
                        });
                    });
                }
            });
            $.data($(this._capacities)[0], 'pct', {
                cloneTree:function(xml) {
                    $('root>Capacity', xml).each(function() {
                        var rt = that.addCapacity((new PCT.capacity()).setRoot(that._capacities).init(this));
                        $('.capacityKey', rt).each(function() {
                            $(this).val(PCT.randomString(15)).change();
                        });
                    });
                }
            });
            $(this._remove).click(function() {
                if (confirm('Remove group?')) {
                    if (confirm('Group will be removed with nested capacities. Removing capacity can result in broken backward compatibility. Remove group?')) {
                        $(that._body).remove();
                    }
                }
            });
            $(this._clone).click(function() {
                $.data($(that._core).parents('.divGroups')[0], 'pct').cloneTree($.parseXML('<root>' + $.data($(that._core)[0], 'pct').getXML() + '</root>'));
            });
            $(this._expand).click(function(arg) {
                $(that._content).toggleClass('hidden');
                if ($(that._content).hasClass('hidden')) {
                    $(that._expand).html('Expand');
                } else {
                    $(that._expand).html('Collapse');
                }
            });
            $(this._name).change(function() {
                $(that._groupTitle).html($(this).val());
            });
            $(this._addGroup).click(function() {
                if ($(that._content).hasClass('hidden')) {
                    $(that._content).removeClass('hidden');
                    $(that._expand).html('Collapse');
                }
                that.addGroup();
            });
            $(this._addCapacity).click(function() {
                if ($(that._content).hasClass('hidden')) {
                    $(that._content).removeClass('hidden');
                    $(that._expand).html('Collapse');
                }
                that.addCapacity();
            });
            $(this._addLink).click(function() {
                if ($(that._content).hasClass('hidden')) {
                    $(that._content).removeClass('hidden');
                    $(that._expand).html('Collapse');
                }
                that.addLink();
            });
            $(this._isRoot).change(function() {
                if ($(this).val() == 'true') {
                    $(that._drag).addClass('hidden');
                    $(that._clone).addClass('hidden');
                    $(that._name).val('CR').change();
                    $(that._content).addClass('hidden');
                    $(that._expand).html('Expand');
                    $(that._settings).addClass('hidden');
                    $(that._groupTitle).html('Capacities Root');
                    $(that._settingsPane).addClass('hidden');
                    $(that._remove).addClass('hidden');
                    $(that._groupTitle).addClass('unclickable');
                } else {
                    $(that._groupTitle).click(function() {
                        $(that._remove).toggleClass('hidden');
                        $(that._settingsPane).toggleClass('hidden');
                    });
                }
            });

            $.extend(this, PCT.base, {
                root:$('<div></div>').append(dom.contents()),
                getHead:function() {
                    return this._head;
                },
                setIsRoot:function(r) {
                    $(this._isRoot).val(r ? 'true' : 'false').change();
                    return this;
                },
                getIsRoot:function() {
                    return $(this._isRoot).val() == 'true';
                },
                init:function(name, shortName, hint, hidden, initialData) {
                    if (name) {
                        $(this._name).val(name).change();
                    }
                    var that = this;
                    if (shortName) {
                        $(this._shortName).val(shortName).change();
                    }
                    if (hint) {
                        $(this._hint).val(hint).change();
                    }
                    if (hidden) {
                        $(this._hidden).prop('checked', (hidden == 'true')).change();
                    }
                    $('>Capacity', initialData).each(function() {
                        that.addCapacity((new PCT.capacity()).setRoot(that._capacities).init(this));
                    });
                    $('>Group', initialData).each(function() {
                        that.addGroup((new PCT.capacitiesGroup()).setRoot(that._groups).init($('>Name', this).text(), $('>ShortName', this).text(), $('>Hint', this).text(), $('>Hidden', this).text(), $('>Capacities', this)).setIsRoot(false));
                    });
                    $(this._settingsPane).addClass('hidden');
                    $(this._remove).addClass('hidden');
                    return this;
                },
                addCapacity:function(capacity) {
                    if (capacity) {
                        $('html, body').animate({
                            scrollTop: $(capacity.getHead()).offset().top
                        }, 000);
                        return capacity.getHead();
                    } else {
                        return this.addCapacity((new PCT.capacity()).setRoot(this._capacities));
                    }
                },
                addLink:function() {
                    return this.addCapacity((new PCT.capacity()).setRoot(this._capacities).setLink());
                },
                addGroup:function(group) {
                    if (group) {
                        $('html, body').animate({
                            scrollTop: $(group.getHead()).offset().top
                        }, 000);
                        return group.getHead();
                    } else {
                        return this.addGroup((new PCT.capacitiesGroup()).setRoot(this._groups).setIsRoot(false));
                    }
                }
            });
        },
        capacity:function(dom) {
            if (!dom) {
                dom = PCT.getTemplate('capacity');
            }
            dom = $('<div></div>').append(dom);
            $.extend(this, {
                _head:$(dom).children().first().get(),
                _body:$(dom).contents(),
                _name:$('#Name', dom).get(),
                _shortName:$('#ShortName', dom).get(),
                _type:$('#Type', dom).get(),
                _key:$('#Key', dom).get(),
                _deprecated:$('#Deprecated', dom).get(),
                _hidden:$('#Hidden', dom).get(),
                _capacityTitle:$('#Title', dom).get(),
                _licenseKey:$('#LicenseKey', dom).get(),
                _addTier:$('#AddTier', dom).get(),
                _settings:$('#Settings', dom).get(),
                _dependencies:$('#Dependencies', dom).get(),
                _addDependency:$('#AddDependency', dom).get(),
                _settingsPane:$('#SettingsPane', dom).get(),
                _tiers:$('#Tiers', dom).get(),
                _expand:$('#Expand', dom).get(),
                _minValue:$('#MinValue', dom).get(),
                _remove:$('#Remove', dom).get(),
                _clone:$('#Clone', dom).get(),
                _core:$('#Core', dom).get(),
                _hint:$('#Hint', dom).get(),
                _linkKey:$('#LinkKey', dom).get(),
                _isLink:false
            });
            var that = this;
            $(this._dependencies, dom).sortable({
                revert:true,
                handle: '.dependencyDrag',
                connectWith: '.divCapacityDependencies'
            });
            $(this._tiers, dom).sortable({
                revert:true,
                handle: '.tierDrag',
                connectWith: '.divTiers'
            });

            $('.linkSetting .settingInput', this._settingsPane).removeClass('validate').change();
            $.data($(this._core)[0], 'pct', {
                getXML:function() {
                    var config = '<Capacity>';
                    config += '<Name>' + $(that._name).val() + '</Name>';
                    config += '<Hint>' + $(that._hint).val() + '</Hint>';
                    config += '<ShortName>' + $(that._shortName).val() + '</ShortName>';
                    config += '<LicenseKey>' + $(that._licenseKey).val() + '</LicenseKey>';
                    config += '<Key>' + $(that._key).val() + '</Key>';
                    if (that._isLink) {
                        config += '<LinkKey>' + $(that._linkKey).val() + '</LinkKey>';
                    } else {
                        config += '<MinValue>' + $(that._minValue).val() + '</MinValue>';
                        config += '<Type>' + $(that._type).val() + '</Type>';
                        config += '<Deprecated>' + ($(that._deprecated).prop('checked') ? 'true' : 'false') + '</Deprecated>';
                        config += '<Hidden>' + ($(that._hidden).prop('checked') ? 'true' : 'false') + '</Hidden>';
                        config += '<Tiers>';
                        $(that._tiers).children().each(function() {
                            if ($(this).hasClass('divTier'))
                                config += $.data($(this)[0], 'pct').getXML();
                        });
                        config += '</Tiers>';
                    }
                    config += '<Dependencies>';
                    $(that._dependencies).children().each(function() {
                        if ($(this).hasClass('requireDependency'))
                            config += '<Require>' + $.data($(this)[0], 'pct').getXML() + '</Require>';
                    });
                    $(that._dependencies).children().each(function() {
                        if ($(this).hasClass('requireCapacityDependency'))
                            config += '<RequireCapacity' + $.data($(this)[0], 'pct').getAttributesString() + '>' + $.data($(this)[0], 'pct').getXML() + '</RequireCapacity>';
                    });
                    config += '</Dependencies>';
                    config += '</Capacity>';
                    return config;
                }
            });
            $(this._remove).click(function() {
                if (confirm('Remove capacity?')) {
                    if (confirm('Removing capacity can result in broken backward compatibility. Remove capacity?')) {
                        $(that._body).remove();
                    }
                }
            });
            $(this._clone).click(function() {
                $.data($(that._core).parents('.divCapacities')[0], 'pct').cloneTree($.parseXML('<root>' + $.data($(that._core)[0], 'pct').getXML() + '</root>'));
            });
            $(this._name).change(function() {
                $(that._capacityTitle).html($(this).val());
            });
            $(this._addDependency).click(function() {
                if ($(that._dependencies).hasClass('hidden')) {
                    $(that._dependencies).removeClass('hidden');
                    $(that._tiers).removeClass('hidden');
                    $(that._expand).html('Collapse');
                }
                that.addDependency();
            });
            $(this._key).val(PCT.randomString(15)).change();
            $(this._expand).click(function(arg) {
                $(that._tiers).toggleClass('hidden');
                $(that._dependencies).toggleClass('hidden');
                if ($(that._tiers).hasClass('hidden')) {
                    $(that._expand).html('Expand');
                } else {
                    $(that._expand).html('Collapse');
                }
            });
            $(this._deprecated).change(function() {
                if ($(this).prop('checked')) {
                    $(that._capacityTitle).addClass('deprecated');
                } else {
                    $(that._capacityTitle).removeClass('deprecated');
                }
            });
            $(this._addTier).click(function() {
                if ($(that._tiers).hasClass('hidden')) {
                    $(that._tiers).removeClass('hidden');
                    $(that._dependencies).removeClass('hidden');
                    $(that._expand).html('Collapse');
                }
                that.addTier();
            });
            $(this._capacityTitle).click(function() {
                $(that._remove).toggleClass('hidden');
                $(that._settingsPane).toggleClass('hidden');
            });
            $.extend(this, PCT.base, {
                root:$('<div></div>').append(dom.contents()),
                getHead:function() {
                    return this._head;
                },
                init:function(initialData) {
                    $(this._name).val($('>Name', initialData).text()).change();
                    $(this._shortName).val($('>ShortName', initialData).text()).change();
                    $(this._licenseKey).val($('>LicenseKey', initialData).text()).change();
                    $(this._hint).val($('>Hint', initialData).text()).change();
                    $(this._key).val('').val($('>Key', initialData).text()).change();
                    if ($('>LinkKey', initialData).length > 0) {
                        this.setLink();
                        $(this._linkKey).val($('>LinkKey', initialData).text()).change();
                    } else {
                        $(this._type).val($('>Type', initialData).text()).change();
                        $(this._minValue).val($('>MinValue', initialData).text()).change();
                        $(this._deprecated).prop('checked', ($('>Deprecated', initialData).text() == 'true')).change();
                        $(this._hidden).prop('checked', ($('>Hidden', initialData).text() == 'true')).change();
                    }
                    $(this._tiers).addClass('hidden');
                    $(this._dependencies).addClass('hidden');
                    $(this._expand).html('Expand');
                    $(this._settingsPane).addClass('hidden');
                    $(this._remove).addClass('hidden');
                    var that = this;
                    $('>Dependencies>Require', initialData).each(function() {
                        that.addDependency((new PCT.capacityDependency()).setRoot(that._dependencies).setType('require').init($(this).text()));
                    });
                    $('>Dependencies>RequireCapacity', initialData).each(function() {
                        that.addDependency((new PCT.capacityDependency()).setRoot(that._dependencies).setType('capacity').setValue($(this).attr('value')).setIncremental($(this).attr('incremental') == 'true').setFreeOfCharge($(this).attr('freeofcharge') == 'true').init($(this).text()));
                    });
                    $('>Tiers>Tier', initialData).each(function() {
                        that.addTier((new PCT.tier()).setRoot(that._tiers).init(this));
                    });
                    return this;
                },
                setLink:function() {
                    this._isLink = true;
                    //$(this._expand).addClass('hidden');
                    $(this._addTier).addClass('hidden');
                    //$(this._tiers).addClass('hidden');

                    $('.capacitySetting .validate', this._settingsPane).removeClass('validate').change();
                    $('.capacitySetting', this._settingsPane).addClass('hidden');
                    $('.linkSetting', this._settingsPane).removeClass('hidden');
                    $('.linkSetting .settingInput', this._settingsPane).addClass('validate').change();
                    return this;
                },
                addTier:function(tier) {
                    if (tier) {
                        $('html, body').animate({
                            scrollTop: $(tier.getHead()).offset().top
                        }, 000);
                    } else {
                        this.addTier((new PCT.tier()).setRoot(this._tiers));
                    }
                },
                addDependency:function(dependency) {
                    if (dependency) {
                        $('html, body').animate({
                            scrollTop: $(dependency.getHead()).offset().top
                        }, 000);
                    } else {
                        this.addDependency((new PCT.capacityDependency()).setRoot(this._dependencies).setType('require'));
                    }
                }
            });
        },
        tier:function(dom) {
            if (!dom) {
                dom = PCT.getTemplate('tier');
            }
            dom = $('<div></div>').append(dom);
            $.extend(this, {
                _head:$(dom).children().first().get(),
                _body:$(dom).contents(),
                _bound:$('#Bound', dom).get(),
                _price:$('#Price', dom).get(),
                _tierTitle:$('#Title', dom).get(),
                _settings:$('#Settings', dom).get(),
                _settingsPane:$('#SettingsPane', dom).get(),
                _remove:$('#Remove', dom).get(),
                _core:$('#Core', dom).get()
            });
            var that = this;
            $.data($(this._core)[0], 'pct', {
                getXML:function() {
                    var config = '<Tier>';
                    config += '<Bound>' + $(that._bound).val() + '</Bound>';
                    config += '<Price>' + $(that._price).val() + '</Price>';
                    config += '</Tier>';
                    return config;
                }
            });
            $(this._remove).click(function() {
                if (confirm('Remove tier?')) {
                    $(that._body).remove();
                }
            });
            $(this._bound).change(function() {
                $(that._tierTitle).html($(this).val() + ' - ' + $(that._price).val());
            });
            $(this._price).change(function() {
                $(that._tierTitle).html($(that._bound).val() + ' - ' + $(this).val());
            });
            $(this._tierTitle).click(function() {
                $(that._remove).toggleClass('hidden');
                $(that._settingsPane).toggleClass('hidden');
            });
            $.extend(this, PCT.base, {
                root:$('<div></div>').append(dom.contents()),
                getHead:function() {
                    return this._head;
                },
                init:function(initialData) {
                    $(this._bound).val($('>Bound', initialData).text()).change();
                    $(this._price).val($('>Price', initialData).text()).change();
                    $(this._settingsPane).addClass('hidden');
                    $(this._remove).addClass('hidden');
                    return this;
                }
            });
        },
        capacityDependency:function(dom) {
            if (!dom) {
                dom = PCT.getTemplate('capacityDependency');
            }
            dom = $('<div></div>').append(dom);
            $.extend(this, {
                _head:$(dom).children().first().get(),
                _body:$(dom).contents(),
                _key:$('#Key', dom).get(),
                _value:$('#Value', dom).get(),
                _incremental:$('#Incremental', dom).get(),
                _freeOfCharge:$('#FreeOfCharge', dom).get(),
                _dependencyBody:$('#DependencyBody', dom).get(),
                _dependencyTitle:$('#Title', dom).get(),
                _settings:$('#Settings', dom).get(),
                _type:$('#Type', dom).get(),
                _settingsPane:$('#SettingsPane', dom).get(),
                _remove:$('#Remove', dom).get(),
                _core:$('#DependencyBody', dom).get(),
                _keyTitle:$('#KeyTitle', dom).get()
            });
            var that = this;
            $.data($(this._core)[0], 'pct', {
                getXML:function() {
                    return $(that._key).val();
                },
                getAttributesString:function() {
                    var rets = '';
                    rets += ' value="' + $(that._value).val() + '"';
                    if ($(that._incremental).prop('checked')) {
                        rets += ' incremental="true"';
                        if ($(that._freeOfCharge).prop('checked')) {
                            rets += ' freeofcharge="true"';
                        }
                    }
                    return rets;
                }
            });
            $(this._incremental).change(function() {
                if ($(this).prop('checked')) {
                    $(that._dependencyBody).addClass('incremental');
                } else {
                    $(that._dependencyBody).removeClass('incremental');
                }
            });
            $(this._remove).click(
                function() {
                    if (confirm('Remove dependency?')) {
                        $(that._body).remove();
                    }
                });
            $(this._key).change(function() {
                $(that._dependencyTitle).html($(this).val());
            });
            $(this._type).change(function() {
                if ($(this).val() == 'require') {
                    $(that._dependencyBody).addClass('requireDependency');
                    $(that._dependencyBody).removeClass('excludeDependency').removeClass('requireCapacityDependency');
                    $(that._keyTitle).html('Key(s):');
                    $(that._value).removeClass('validate').change();
                } else if ($(this).val() == 'exclude') {
                    $(that._dependencyBody).addClass('excludeDependency');
                    $(that._dependencyBody).removeClass('requireDependency').removeClass('requireCapacityDependency');
                    $(that._keyTitle).html('Key:');
                    $(that._value).removeClass('validate').change();
                } else if ($(this).val() == 'capacity') {
                    $(that._dependencyBody).addClass('requireCapacityDependency');
                    $(that._keyTitle).html('Key:');
                    $(that._value).addClass('validate').change();
                    $(that._dependencyBody).removeClass('requireDependency').removeClass('excludeDependency');
                }
            });
            $(this._dependencyTitle).click(
                function() {
                    $(that._remove).toggleClass('hidden');
                    $(that._settingsPane).toggleClass('hidden');
                });
            $.extend(this, PCT.base, {
                root:$('<div></div>').append(dom.contents()),
                getHead:function() {
                    return this._head;
                },
                init:function(initialData) {
                    $(this._key).val(initialData).change();
                    $(this._settingsPane).addClass('hidden');
                    $(this._remove).addClass('hidden');
                    return this;
                },
                setType:function(type) {
                    $(this._type).val(type).change();
                    return this;
                },
                setValue:function(value) {
                    $(this._value).val(value).change();
                    return this;
                },
                setIncremental:function(incremental) {
                    $(this._incremental).prop('checked', incremental).change();
                    return this;
                },
                setFreeOfCharge:function(freeOfCharge) {
                    $(this._freeOfCharge).prop('checked', freeOfCharge).change();
                    return this;
                }
            });
        },
        region:function(dom) {
            if (!dom) {
                dom = PCT.getTemplate('region');
            }
            dom = $('<div></div>').append(dom);
            $.extend(this, {
                _head:$(dom).children().first().get(),
                _body:$(dom).contents(),
                _name:$('#Name', dom).get(),
                _rate:$('#Rate', dom).get(),
                _mdRate:$('#MDRate', dom).get(),
                _onsiteDailyCost:$('#OnsiteDailyCost', dom).get(),
                _tripPrice:$('#TripPrice', dom).get(),
                _regionTitle:$('#Title', dom).get(),
                _key:$('#Key', dom).get(),
                _settings:$('#Settings', dom).get(),
                _settingsPane:$('#SettingsPane', dom).get(),
                _expand:$('#Expand', dom).get(),
                _remove:$('#Remove', dom).get(),
                _defaultCurrency:$('#DefaultCurrency', dom).get(),
                _regionProducts:$('#RegionProducts', dom).get(),
                _addRegionProduct:$('#AddRegionProduct', dom).get(),
                _clone:$('#Clone', dom).get(),
                _core:$('#Core', dom).get()
            });
            var that = this;
            $(this._regionProducts, dom).sortable({
                revert:true,
                handle: '.regionProductDrag',
                connectWith: '.divRegionProducts'
            });
            $.data($(this._core)[0], 'pct', {
                getXML:function() {
                    var config = '<Region>';
                    config += '<Name>' + $(that._name).val() + '</Name>';
                    config += '<Rate>' + $(that._rate).val() + '</Rate>';
                    config += '<MDRate>' + $(that._mdRate).val() + '</MDRate>';
                    config += '<OnsiteDailyCost>' + $(that._onsiteDailyCost).val() + '</OnsiteDailyCost>';
                    config += '<TripPrice>' + $(that._tripPrice).val() + '</TripPrice>';
                    config += '<DefaultCurrency>' + $(that._defaultCurrency).val() + '</DefaultCurrency>';
                    config += '<Key>' + $(that._key).val() + '</Key>';
                    config += '<Products>';
                    $(that._regionProducts).children().each(function() {
                        if ($(this).hasClass('divRegionProduct'))
                            config += $.data($(this)[0], 'pct').getXML();
                    });
                    config += '</Products>';
                    config += '</Region>';
                    return config;
                }
            });
            $(this._clone).click(function() {
                $.data($(that._core).parents('.divModelRegions')[0], 'pct').cloneTree($.parseXML('<root>' + $.data($(that._core)[0], 'pct').getXML() + '</root>'));
            });
            $(this._remove).click(function() {
                if (confirm('Remove region?')) {
                    if (confirm('Removing region can result in broken backward compatibility. Remove region?')) {
                        $(that._body).remove();
                    }
                }
            });
            $(this._name).change(function() {
                $(that._regionTitle).html($(this).val());
            });
            $(this._expand).click(function(arg) {
                $(that._regionProducts).toggleClass('hidden');
                if ($(that._regionProducts).hasClass('hidden')) {
                    $(that._expand).html('Expand');
                } else {
                    $(that._expand).html('Collapse');
                }
            });
            $(this._addRegionProduct).click(function() {
                if ($(that._regionProducts).hasClass('hidden')) {
                    $(that._regionProducts).removeClass('hidden');
                    $(that._expand).html('Collapse');
                }
                that.addRegionProduct();
            });
            $(this._key).val(PCT.randomString(15)).change();
            $(this._regionTitle).click(function() {
                $(that._remove).toggleClass('hidden');
                $(that._settingsPane).toggleClass('hidden');
            });
            $.extend(this, PCT.base, {
                root:$('<div></div>').append(dom.contents()),
                getHead:function() {
                    return this._head;
                },
                init:function(initialData) {
                    $(this._name).val($('>Name', initialData).text()).change();
                    $(this._rate).val($('>Rate', initialData).text()).change();
                    $(this._mdRate).val($('>MDRate', initialData).text()).change();
                    $(this._onsiteDailyCost).val($('>OnsiteDailyCost', initialData).text()).change();
                    $(this._tripPrice).val($('>TripPrice', initialData).text()).change();
                    $(this._regionProducts).addClass('hidden');
                    $(this._defaultCurrency).val($('>DefaultCurrency', initialData).text()).change();
                    $(this._key).val('').val($('>Key', initialData).text()).change();
                    $(this._settingsPane).addClass('hidden');
                    $(this._remove).addClass('hidden');
                    $(this._expand).html('Expand');
                    $('>Products>Product', initialData).each(function() {
                        that.addRegionProduct((new PCT.regionProduct()).setRoot(that._regionProducts).init(this));
                    });
                    return this;
                },
                addRegionProduct:function(product) {
                    if (product) {
                        $('html, body').animate({
                            scrollTop: $(product.getHead()).offset().top
                        }, 000);
                    } else {
                        this.addRegionProduct((new PCT.regionProduct()).setRoot(this._regionProducts));
                    }
                }
            });
        },
        regionProduct:function(dom) {
            if (!dom) {
                dom = PCT.getTemplate('regionProduct');
            }
            dom = $('<div></div>').append(dom);
            $.extend(this, {
                _head:$(dom).children().first().get(),
                _body:$(dom).contents(),
                _key:$('#Key', dom).get(),
                _rate:$('#Rate', dom).get(),
                _regionProductTitle:$('#Title', dom).get(),
                _settings:$('#Settings', dom).get(),
                _settingsPane:$('#SettingsPane', dom).get(),
                _remove:$('#Remove', dom).get(),
                _core:$('#Core', dom).get()
            });
            var that = this;
            $.data($(this._core)[0], 'pct', {
                getXML:function() {
                    var config = '<Product>';
                    config += '<Key>' + $(that._key).val() + '</Key>';
                    config += '<Rate>' + $(that._rate).val() + '</Rate>';
                    config += '</Product>';
                    return config;
                }
            });
            $(this._remove).click(function() {
                if (confirm('Remove product?')) {
                    $(that._body).remove();
                }
            });
            $(this._key).change(function() {
                $(that._regionProductTitle).html($(this).val());
            });
            $(this._regionProductTitle).click(function() {
                $(that._remove).toggleClass('hidden');
                $(that._settingsPane).toggleClass('hidden');
            });
            $.extend(this, PCT.base, {
                root:$('<div></div>').append(dom.contents()),
                getHead:function() {
                    return this._head;
                },
                init:function(initialData) {
                    $(this._key).val($('>Key', initialData).text()).change();
                    $(this._rate).val($('>Rate', initialData).text()).change();
                    $(this._settingsPane).addClass('hidden');
                    $(this._remove).addClass('hidden');
                    return this;
                }
            });
        },
        supportPlan:function(dom) {
            if (!dom) {
                dom = PCT.getTemplate('supportPlan');
            }
            dom = $('<div></div>').append(dom);
            $.extend(this, {
                _head:$(dom).children().first().get(),
                _body:$(dom).contents(),
                _name:$('#Name', dom).get(),
                _rate:$('#Rate', dom).get(),
                _key:$('#Key', dom).get(),
                _supportPlanTitle:$('#Title', dom).get(),
                _addSupportPlanRegion:$('#AddSupportPlanRegion', dom).get(),
                _settings:$('#Settings', dom).get(),
                _settingsPane:$('#SettingsPane', dom).get(),
                _supportPlanRegions:$('#SupportPlanRegions', dom).get(),
                _expand:$('#Expand', dom).get(),
                _remove:$('#Remove', dom).get(),
                _default:$('#Default', dom).get(),
                _minPrice:$('#MinPrice', dom).get(),
                _clone:$('#Clone', dom).get(),
                _core:$('#Core', dom).get()
            });
            var that = this;
            $(this._supportPlanRegions, dom).sortable({
                revert:true,
                handle: '.supportPlanRegionDrag',
                connectWith: '.divSupportPlanRegions'
            });
            $(this._minPrice).val(0).change();
            $.data($(this._core)[0], 'pct', {
                getXML:function() {
                    var config = '<SupportPlan>';
                    config += '<Name>' + $(that._name).val() + '</Name>';
                    config += '<Key>' + $(that._key).val() + '</Key>';
                    config += '<Default>' + ($(that._default).prop('checked') ? 'true' : 'false') + '</Default>';
                    config += '<Rate>' + $(that._rate).val() + '</Rate>';
                    config += '<MinPrice>' + $(that._minPrice).val() + '</MinPrice>';
                    config += '<Regions>';
                    $(that._supportPlanRegions).children().each(function() {
                        if ($(this).hasClass('divSupportPlanRegion'))
                            config += $.data($(this)[0], 'pct').getXML();
                    });
                    config += '</Regions>';
                    config += '</SupportPlan>';
                    return config;
                }
            });
            $(this._remove).click(function() {
                if (confirm('Remove support plan?')) {
                    if (confirm('Removing support plan can result in broken backward compatibility. Remove support plan?')) {
                        $(that._body).remove();
                    }
                }
            });
            $(this._clone).click(function() {
                $.data($(that._core).parents('.divModelSupportPlans')[0], 'pct').cloneTree($.parseXML('<root>' + $.data($(that._core)[0], 'pct').getXML() + '</root>'));
            });
            $(this._name).change(function() {
                $(that._supportPlanTitle).html($(this).val());
            });
            $(this._key).val(PCT.randomString(15)).change();
            $(this._expand).click(function(arg) {
                $(that._supportPlanRegions).toggleClass('hidden');
                if ($(that._supportPlanRegions).hasClass('hidden')) {
                    $(that._expand).html('Expand');
                } else {
                    $(that._expand).html('Collapse');
                }
            });
            $(this._addSupportPlanRegion).click(function() {
                if ($(that._supportPlanRegions).hasClass('hidden')) {
                    $(that._supportPlanRegions).removeClass('hidden');
                    $(that._expand).html('Collapse');
                }
                that.addSupportPlanRegion();
            });
            $(this._supportPlanTitle).click(function() {
                $(that._remove).toggleClass('hidden');
                $(that._settingsPane).toggleClass('hidden');
            });
            $.extend(this, PCT.base, {
                root:$('<div></div>').append(dom.contents()),
                getHead:function() {
                    return this._head;
                },
                init:function(initialData) {
                    $(this._name).val($('>Name', initialData).text()).change();
                    $(this._rate).val($('>Rate', initialData).text()).change();
                    $(this._default).prop('checked', ($('>Default', initialData).text() == 'true')).change();
                    $(this._key).val('').val($('>Key', initialData).text()).change();
                    $(this._minPrice).val('').val($('>MinPrice', initialData).text()).change();
                    $(this._settingsPane).addClass('hidden');
                    $(this._supportPlanRegions).addClass('hidden');
                    $(this._expand).html('Expand');
                    $(this._remove).addClass('hidden');
                    var that = this;
                    $('>Regions>Region', initialData).each(function() {
                        that.addSupportPlanRegion((new PCT.supportPlanRegion()).setRoot(that._supportPlanRegions).init(this));
                    });
                    return this;
                },
                addSupportPlanRegion:function(region) {
                    if (region) {
                        $('html, body').animate({
                            scrollTop: $(region.getHead()).offset().top
                        }, 000);
                    } else {
                        this.addSupportPlanRegion((new PCT.supportPlanRegion()).setRoot(this._supportPlanRegions));
                    }
                }
            });
        },
        supportPlanRegion:function(dom) {
            if (!dom) {
                dom = PCT.getTemplate('supportPlanRegion');
            }
            dom = $('<div></div>').append(dom);
            $.extend(this, {
                _head:$(dom).children().first().get(),
                _body:$(dom).contents(),
                _key:$('#Key', dom).get(),
                _rate:$('#Rate', dom).get(),
                _supportPlanRegionTitle:$('#Title', dom).get(),
                _settings:$('#Settings', dom).get(),
                _settingsPane:$('#SettingsPane', dom).get(),
                _remove:$('#Remove', dom).get(),
                _core:$('#Core', dom).get()
            });
            var that = this;
            $.data($(this._core)[0], 'pct', {
                getXML:function() {
                    var config = '<Region>';
                    config += '<Key>' + $(that._key).val() + '</Key>';
                    config += '<Rate>' + $(that._rate).val() + '</Rate>';
                    config += '</Region>';
                    return config;
                }
            });
            $(this._remove).click(function() {
                if (confirm('Remove region?')) {
                    $(that._body).remove();
                }
            });
            $(this._key).change(function() {
                $(that._supportPlanRegionTitle).html($(this).val());
            });
            $(this._supportPlanRegionTitle).click(function() {
                $(that._remove).toggleClass('hidden');
                $(that._settingsPane).toggleClass('hidden');
            });
            $.extend(this, PCT.base, {
                root:$('<div></div>').append(dom.contents()),
                getHead:function() {
                    return this._head;
                },
                init:function(initialData) {
                    $(this._key).val($('>Key', initialData).text()).change();
                    $(this._rate).val($('>Rate', initialData).text()).change();
                    $(this._settingsPane).addClass('hidden');
                    $(this._remove).addClass('hidden');
                    return this;
                }
            });
        },
        authLevel:function(dom) {
            if (!dom) {
                dom = PCT.getTemplate('authLevel');
            }
            dom = $('<div></div>').append(dom);
            $.extend(this, {
                _head:$(dom).children().first().get(),
                _body:$(dom).contents(),
                _name:$('#Name', dom).get(),
                _description:$('#Description', dom).get(),
                _memoText:$('#MemoText', dom).get(),
                _memoHeader:$('#MemoHeader', dom).get(),
                _key:$('#Key', dom).get(),
                _authLevelTitle:$('#Title', dom).get(),
                _addAuthLevelLevel:$('#AddAuthLevelLevel', dom).get(),
                _settings:$('#Settings', dom).get(),
                _settingsPane:$('#SettingsPane', dom).get(),
                _authLevelLevels:$('#AuthLevelLevels', dom).get(),
                _expand:$('#Expand', dom).get(),
                _remove:$('#Remove', dom).get(),
                _clone:$('#Clone', dom).get(),
                _core:$('#Core', dom).get()
            });
            var that = this;
            $(this._authLevelLevels, dom).sortable({
                revert:true,
                handle: '.authLevelLevelDrag',
                connectWith: '.divAuthLevelLevels'
            });
            $.data($(this._authLevelLevels)[0], 'pct', {
                cloneTree:function(xml) {
                    $('root>Level', xml).each(function() {
                        var rt = that.addAuthLevelLevel((new PCT.authLevelLevel()).setRoot(that._authLevelLevels).init(this));
                        //var rt = that.addGroup((new PCT.capacitiesGroup()).setRoot(that._groups).init($('>Name', this).text(), $('>ShortName', this).text(), $('>Hint', this).text(), $('>Hidden', this).text(), $('>Capacities', this)).setIsRoot(false));
                        $('.authLevelLevelKey', rt).each(function() {
                            $(this).val(PCT.randomString(15)).change();
                        });
                    });
                }
            });
            $(this._minPrice).val(0).change();
            $.data($(this._core)[0], 'pct', {
                getXML:function() {
                    var config = '<AuthLevel>';
                    config += '<Name>' + $(that._name).val() + '</Name>';
                    config += '<Key>' + $(that._key).val() + '</Key>';
                    config += '<Description>' + $(that._description).val() + '</Description>';
                    config += '<MemoText>' + $(that._memoText).val() + '</MemoText>';
                    config += '<MemoHeader>' + $(that._memoHeader).val() + '</MemoHeader>';
                    config += '<Levels>';
                    $(that._authLevelLevels).children().each(function() {
                        if ($(this).hasClass('divAuthLevelLevel'))
                            config += $.data($(this)[0], 'pct').getXML();
                    });
                    config += '</Levels>';
                    config += '</AuthLevel>';
                    return config;
                }
            });
            $(this._remove).click(function() {
                if (confirm('Remove authority level?')) {
                    if (confirm('Removing authority level can result in broken backward compatibility. Remove authority level?')) {
                        $(that._body).remove();
                    }
                }
            });
            $(this._clone).click(function() {
                $.data($(that._core).parents('.divModelAuthLevels')[0], 'pct').cloneTree($.parseXML('<root>' + $.data($(that._core)[0], 'pct').getXML() + '</root>'));
            });
            $(this._name).change(function() {
                $(that._authLevelTitle).html($(this).val());
            });
            $(this._key).val(PCT.randomString(15)).change();
            $(this._expand).click(function(arg) {
                $(that._authLevelLevels).toggleClass('hidden');
                if ($(that._authLevelLevels).hasClass('hidden')) {
                    $(that._expand).html('Expand');
                } else {
                    $(that._expand).html('Collapse');
                }
            });
            $(this._addAuthLevelLevel).click(function() {
                if ($(that._authLevelLevels).hasClass('hidden')) {
                    $(that._authLevelLevels).removeClass('hidden');
                    $(that._expand).html('Collapse');
                }
                that.addAuthLevelLevel();
            });
            $(this._authLevelTitle).click(function() {
                $(that._remove).toggleClass('hidden');
                $(that._settingsPane).toggleClass('hidden');
            });
            $.extend(this, PCT.base, {
                root:$('<div></div>').append(dom.contents()),
                getHead:function() {
                    return this._head;
                },
                init:function(initialData) {
                    $(this._name).val($('>Name', initialData).text()).change();
                    $(this._description).val($('>Description', initialData).text()).change();
                    $(this._memoText).val($('>MemoText', initialData).text()).change();
                    $(this._memoHeader).val($('>MemoHeader', initialData).text()).change();
                    $(this._key).val('').val($('>Key', initialData).text()).change();
                    $(this._settingsPane).addClass('hidden');
                    $(this._authLevelLevels).addClass('hidden');
                    $(this._expand).html('Expand');
                    $(this._remove).addClass('hidden');
                    var that = this;
                    $('>Levels>Level', initialData).each(function() {
                        that.addAuthLevelLevel((new PCT.authLevelLevel()).setRoot(that._authLevelLevels).init(this));
                    });
                    return this;
                },
                addAuthLevelLevel:function(level) {
                    if (level) {
                        $('html, body').animate({
                            scrollTop: $(level.getHead()).offset().top
                        }, 000);
                        return level.getHead();
                    } else {
                        return this.addAuthLevelLevel((new PCT.authLevelLevel()).setRoot(this._authLevelLevels));
                    }
                }
            });
        },
        authLevelLevel:function(dom) {
            if (!dom) {
                dom = PCT.getTemplate('authLevelLevel');
            }
            dom = $('<div></div>').append(dom);
            $.extend(this, {
                _head:$(dom).children().first().get(),
                _body:$(dom).contents(),
                _key:$('#Key', dom).get(),
                _priority:$('#Priority', dom).get(),
                _description:$('#Description', dom).get(),
                _authLevelLevelTitle:$('#Title', dom).get(),
                _clone:$('#Clone', dom).get(),
                _name:$('#Name', dom).get(),
                _settings:$('#Settings', dom).get(),
                _settingsPane:$('#SettingsPane', dom).get(),
                _remove:$('#Remove', dom).get(),
                _core:$('#Core', dom).get()
            });
            var that = this;
            $(this._clone).click(function() {
                $.data($(that._core).parents('.divAuthLevelLevels')[0], 'pct').cloneTree($.parseXML('<root>' + $.data($(that._core)[0], 'pct').getXML() + '</root>'));
            });
            $.data($(this._core)[0], 'pct', {
                getXML:function() {
                    var config = '<Level>';
                    config += '<Name>' + $(that._name).val() + '</Name>';
                    config += '<Key>' + $(that._key).val() + '</Key>';
                    config += '<Description>' + $(that._description).val() + '</Description>';
                    config += '<Priority>' + $(that._priority).val() + '</Priority>';
                    config += '</Level>';
                    return config;
                }
            });
            $(this._remove).click(function() {
                if (confirm('Remove level?')) {
                    $(that._body).remove();
                }
            });
            $(this._name).change(function() {
                $(that._authLevelLevelTitle).html($(this).val());
            });
            $(this._key).val(PCT.randomString(15)).change();
            $(this._authLevelLevelTitle).click(function() {
                $(that._remove).toggleClass('hidden');
                $(that._settingsPane).toggleClass('hidden');
            });
            $.extend(this, PCT.base, {
                root:$('<div></div>').append(dom.contents()),
                getHead:function() {
                    return this._head;
                },
                init:function(initialData) {
                    $(this._name).val($('>Name', initialData).text()).change();
                    $(this._description).val($('>Description', initialData).text()).change();
                    $(this._key).val($('>Key', initialData).text()).change();
                    $(this._priority).val($('>Priority', initialData).text()).change();
                    $(this._settingsPane).addClass('hidden');
                    $(this._remove).addClass('hidden');
                    return this;
                }
            });
        },
        servicesGroup:function(dom) {
            if (!dom) {
                dom = PCT.getTemplate('servicesGroup');
            }
            dom = $('<div></div>').append(dom);
            $.extend(this, {
                _head:$(dom).children().first().get(),
                _body:$(dom).contents(),
                _name:$('#Name', dom).get(),
                _shortName:$('#ShortName', dom).get(),
                _key:$('#Key', dom).get(),
                _content:$('#Content', dom).get(),
                _services:$('#Services', dom).get(),
                _groups:$('#Groups', dom).get(),
                _groupTitle:$('#GroupTitle', dom).get(),
                _settings:$('#Settings', dom).get(),
                _settingsPane:$('#SettingsPane', dom).get(),
                _addGroup:$('#AddGroup', dom).get(),
                _addService:$('#AddService', dom).get(),
                _expand:$('#Expand', dom).get(),
                _hidden:$('#Hidden', dom).get(),
                _remove:$('#Remove', dom).get(),
                _core:$('#Core', dom).get(),
                _clone:$('#Clone', dom).get(),
                _hint:$('#Hint', dom).get(),
                _drag:$('#Drag', dom).get()
            });
            var that = this;
            $(this._services, dom).sortable({
                revert:true,
                handle: '.serviceDrag',
                connectWith: '.divServices'
            });
            $(this._groups, dom).sortable({
                revert:true,
                handle: '.groupDrag',
                connectWith: '.divSGroups',
                start:function() {
                    //that.updateTotal();
                },
                update:function() {
                    //that.updateTotal();
                }
            });
            $(this._key).val(PCT.randomString(15)).change();
            $.data($(this._core)[0], 'pct', {
                getXML:function() {
                    var config = '';
                    config += '<Group>';
                    config += '<Name>' + $(that._name).val() + '</Name>';
                    config += '<ShortName>' + $(that._shortName).val() + '</ShortName>';
                    config += '<Hidden>' + ($(that._hidden).prop('checked') ? 'true' : 'false') + '</Hidden>';
                    config += '<Hint>' + $(that._hint).val() + '</Hint>';
                    config += '<Key>' + $(that._key).val() + '</Key>';
                    config += '<Services>';
                    $(that._services).children().each(function() {
                        if ($(this).hasClass('divService'))
                            config += $.data($(this)[0], 'pct').getXML();
                    });
                    $(that._groups).children().each(function() {
                        if ($(this).hasClass('divServicesGroup'))
                            config += $.data($(this)[0], 'pct').getXML();
                    });
                    config += '</Services>';
                    config += '</Group>';
                    return config;
                }
            });
            $.data($(this._groups)[0], 'pct', {
                cloneTree:function(xml) {
                    $('root>Group', xml).each(function() {
                        var rt = that.addGroup((new PCT.servicesGroup()).setRoot(that._groups).init($('>Name', this).text(), $('>ShortName', this).text(), $('>Hint', this).text(), $('>Hidden', this).text(), $('>Key', this).text(), $('>Services', this)));
                        $('.serviceKey', rt).each(function() {
                            $(this).val(PCT.randomString(15)).change();
                        });
                        $('.groupKey', rt).each(function() {
                            $(this).val(PCT.randomString(15)).change();
                        });
                    });
                    //that.updateTotal();
                }
            });
            $.data($(this._services)[0], 'pct', {
                cloneTree:function(xml) {
                    $('root>Service', xml).each(function() {
                        var rt = that.addService((new PCT.service()).setRoot(that._services).init(this));
                        $('.serviceKey', rt).each(function() {
                            $(this).val(PCT.randomString(15)).change();
                        });
                    });
                    //that.updateTotal();
                }
            });
            $(this._remove).click(function() {
                if (confirm('Remove group?')) {
                    if (confirm('Group will be removed with nested services. Removing service can result in broken backward compatibility. Remove group?')) {
                        //$('.moduleWeight', that._content).val(0);
                        //that.updateTotal();
                        $(that._body).remove();
                    }
                }
            });
            $(this._clone).click(function() {
                $.data($(that._core).parents('.divGroups')[0], 'pct').cloneTree($.parseXML('<root>' + $.data($(that._core)[0], 'pct').getXML() + '</root>'));
            });
            $(this._expand).click(function(arg) {
                $(that._content).toggleClass('hidden');
                if ($(that._content).hasClass('hidden')) {
                    $(that._expand).html('Expand');
                } else {
                    $(that._expand).html('Collapse');
                }
            });
            $(this._addGroup).click(function() {
                if ($(that._content).hasClass('hidden')) {
                    $(that._content).removeClass('hidden');
                    $(that._expand).html('Collapse');
                }
                that.addGroup();
            });
            $(this._addService).click(function() {
                if ($(that._content).hasClass('hidden')) {
                    $(that._content).removeClass('hidden');
                    $(that._expand).html('Collapse');
                }
                that.addService();
            });
            $(this._name).change(function() {
                $(that._groupTitle).html($(this).val());
            });

            $(this._groupTitle).click(function() {
                $(that._remove).toggleClass('hidden');
                $(that._settingsPane).toggleClass('hidden');
            });

            $.extend(this, PCT.base, {
                root:$('<div></div>').append(dom.contents()),
                getHead:function() {
                    return this._head;
                },
                init:function(name, shortName, hint, hidden, key, initialData) {
                    if (name) {
                        $(this._name).val(name).change();
                    }
                    $(this._key).val('').val(key).change();
                    var that = this;
                    if (shortName) {
                        $(this._shortName).val(shortName).change();
                    }
                    if (hidden) {
                        $(this._hidden).prop('checked', (hidden == 'true')).change();
                    }
                    if (hint) {
                        $(this._hint).val(hint).change();
                    }
                    $('>Service', initialData).each(function() {
                        that.addService((new PCT.service()).setRoot(that._services).init(this));
                    });
                    $('>Group', initialData).each(function() {
                        that.addGroup((new PCT.servicesGroup()).setRoot(that._groups).init($('>Name', this).text(), $('>ShortName', this).text(), $('>Hint', this).text(), $('>Hidden', this).text(), $('>Key', this).text(), $('>Services', this)));
                    });
                    $(this._settingsPane).addClass('hidden');
                    $(this._remove).addClass('hidden');
                    return this;
                },
                addService:function(service) {
                    if (service) {
                        $('html, body').animate({
                            scrollTop: $(service.getHead()).offset().top
                        }, 000);
                        return service.getHead();
                    } else {
                        return this.addService((new PCT.service()).setRoot(this._services));
                    }
                },
                addGroup:function(group) {
                    if (group) {
                        $('html, body').animate({
                            scrollTop: $(group.getHead()).offset().top
                        }, 000);
                        return group.getHead();
                    } else {
                        return this.addGroup((new PCT.servicesGroup()).setRoot(this._groups));
                    }
                }
            });
        },
        service:function(dom) {
            if (!dom) {
                dom = PCT.getTemplate('service');
            }
            dom = $('<div></div>').append(dom);
            $.extend(this, {
                _head:$(dom).children().first().get(),
                _body:$(dom).contents(),
                _name:$('#Name', dom).get(),
                _shortName:$('#ShortName', dom).get(),
                _key:$('#Key', dom).get(),
                _maxMD:$('#MaxMD', dom).get(),
                _minMD:$('#MinMD', dom).get(),
                _hidden:$('#Hidden', dom).get(),
                _serviceTitle:$('#Title', dom).get(),
                _settings:$('#Settings', dom).get(),
                _settingsPane:$('#SettingsPane', dom).get(),
                _remove:$('#Remove', dom).get(),
                _clone:$('#Clone', dom).get(),
                _hint:$('#Hint', dom).get(),
                _core:$('#Core', dom).get()
            });
            var that = this;
            $(this._clone).click(function() {
                $.data($(that._core).parents('.divServices')[0], 'pct').cloneTree($.parseXML('<root>' + $.data($(that._core)[0], 'pct').getXML() + '</root>'));
            });
            $.data($(this._core)[0], 'pct', {
                getXML:function() {
                    var config = '<Service>';
                    config += '<Name>' + $(that._name).val() + '</Name>';
                    config += '<Hint>' + $(that._hint).val() + '</Hint>';
                    config += '<ShortName>' + $(that._shortName).val() + '</ShortName>';
                    config += '<Key>' + $(that._key).val() + '</Key>';
                    config += '<MaxMD>' + $(that._maxMD).val() + '</MaxMD>';
                    config += '<MinMD>' + $(that._minMD).val() + '</MinMD>';
                    config += '<Hidden>' + ($(that._hidden).prop('checked') ? 'true' : 'false') + '</Hidden>';
                    config += '</Service>';
                    return config;
                }
            });
            $(this._remove).click(function() {
                if (confirm('Remove service?')) {
                    if (confirm('Removing service can result in broken backward compatibility. Remove service?')) {
                        $(that._body).remove();
                    }
                }
            });
            $(this._name).change(function() {
                $(that._serviceTitle).html($(this).val());
            });
            $(this._key).val(PCT.randomString(15)).change();
            $(this._serviceTitle).click(function() {
                $(that._remove).toggleClass('hidden');
                $(that._settingsPane).toggleClass('hidden');
            });
            $.extend(this, PCT.base, {
                root:$('<div></div>').append(dom.contents()),
                getHead:function() {
                    return this._head;
                },
                init:function(initialData) {
                    $(this._name).val($('>Name', initialData).text()).change();
                    $(this._hint).val($('>Hint', initialData).text()).change();
                    $(this._shortName).val($('>ShortName', initialData).text()).change();
                    $(this._key).val('').val($('>Key', initialData).text()).change();
                    $(this._maxMD).val($('>MaxMD', initialData).text()).change();
                    $(this._minMD).val($('>MinMD', initialData).text()).change();
                    $(this._hidden).prop('checked', ($('>Hidden', initialData).text() == 'true')).change();
                    $(this._settingsPane).addClass('hidden');
                    $(this._dependencies).addClass('hidden');
                    $(this._remove).addClass('hidden');
                    return this;
                }
            });
        },
        currency:function(dom) {
            if (!dom) {
                dom = PCT.getTemplate('currency');
            }
            dom = $('<div></div>').append(dom);
            $.extend(this, {
                _head:$(dom).children().first().get(),
                _body:$(dom).contents(),
                _name:$('#Name', dom).get(),
                _symbol:$('#Symbol', dom).get(),
                _currencyTitle:$('#Title', dom).get(),
                _addCurrencyRegion:$('#AddCurrencyRegion', dom).get(),
                _settings:$('#Settings', dom).get(),
                _settingsPane:$('#SettingsPane', dom).get(),
                _currencyRegions:$('#CurrencyRegions', dom).get(),
                _expand:$('#Expand', dom).get(),
                _remove:$('#Remove', dom).get(),
                _rate:$('#Rate', dom).get(),
                _rateTitle:$('#RateTitle', dom).get(),
                _clone:$('#Clone', dom).get(),
                _core:$('#Core', dom).get()
            });
            var that = this;
            $(this._currencyRegions, dom).sortable({
                revert:true,
                handle: '.currencyRegionDrag',
                connectWith: '.divCurrencyRegions'
            });
            $.data($(this._core)[0], 'pct', {
                getXML:function() {
                    var config = '<Currency>';
                    config += '<Name>' + $(that._name).val() + '</Name>';
                    config += '<Symbol>' + $(that._symbol).val() + '</Symbol>';
                    config += '<Rate>' + $(that._rate).val() + '</Rate>';
                    config += '<Regions>';
                    $(that._currencyRegions).children().each(function() {
                        if ($(this).hasClass('divCurrencyRegion'))
                            config += $.data($(this)[0], 'pct').getXML();
                    });
                    config += '</Regions>';
                    config += '</Currency>';
                    return config;
                }
            });
            $(this._remove).click(function() {
                if (confirm('Remove currency?')) {
                    if (confirm('Removing currency can result in broken backward compatibility. Remove currency?')) {
                        $(that._body).remove();
                    }
                }
            });
            $(this._clone).click(function() {
                $.data($(that._core).parents('.divModelCurrencies')[0], 'pct').cloneTree($.parseXML('<root>' + $.data($(that._core)[0], 'pct').getXML() + '</root>'));
            });
            $(this._name).change(function() {
                $(that._currencyTitle).html($(this).val());
                $(that._rateTitle).html('Rate (USD/' + $(this).val() + '):');
            });
            $(this._name).val('').change();
            $(this._expand).click(function(arg) {
                $(that._currencyRegions).toggleClass('hidden');
                if ($(that._currencyRegions).hasClass('hidden')) {
                    $(that._expand).html('Expand');
                } else {
                    $(that._expand).html('Collapse');
                }
            });
            $(this._addCurrencyRegion).click(function() {
                if ($(that._currencyRegions).hasClass('hidden')) {
                    $(that._currencyRegions).removeClass('hidden');
                    $(that._expand).html('Collapse');
                }
                that.addCurrencyRegion();
            });
            $(this._currencyTitle).click(function() {
                $(that._remove).toggleClass('hidden');
                $(that._settingsPane).toggleClass('hidden');
            });
            $.extend(this, PCT.base, {
                root:$('<div></div>').append(dom.contents()),
                getHead:function() {
                    return this._head;
                },
                init:function(initialData) {
                    $(this._name).val($('>Name', initialData).text()).change();
                    $(this._symbol).val($('>Symbol', initialData).text()).change();
                    $(this._rate).val($('>Rate', initialData).text()).change();
                    $(this._settingsPane).addClass('hidden');
                    $(this._currencyRegions).addClass('hidden');
                    $(this._expand).html('Expand');
                    $(this._remove).addClass('hidden');
                    var that = this;
                    $('>Regions>Region', initialData).each(function() {
                        that.addCurrencyRegion((new PCT.currencyRegion()).setRoot(that._currencyRegions).init(this));
                    });
                    return this;
                },
                addCurrencyRegion:function(region) {
                    if (region) {
                        $('html, body').animate({
                            scrollTop: $(region.getHead()).offset().top
                        }, 000);
                    } else {
                        this.addCurrencyRegion((new PCT.currencyRegion()).setRoot(this._currencyRegions));
                    }
                }
            });
        },
        recommendation:function(dom) {
            if (!dom) {
                dom = PCT.getTemplate('recommendation');
            }
            dom = $('<div></div>').append(dom);
            $.extend(this, {
                _head:$(dom).children().first().get(),
                _body:$(dom).contents(),
                _key:$('#Key', dom).get(),
                _name:$('#Name', dom).get(),
                _percentageKeys:$('#PercentageKeys', dom).get(),
                _mdValue:$('#MDValue', dom).get(),
                _referenceKey:$('#ReferenceKey', dom).get(),
                _recommendationTitle:$('#Title', dom).get(),
                _recommendationType:$('#RecommendationType', dom).get(),
                _settings:$('#Settings', dom).get(),
                _settingsPane:$('#SettingsPane', dom).get(),
                _remove:$('#Remove', dom).get(),
                _clone:$('#Clone', dom).get(),
                _hint:$('#Hint', dom).get(),
                _core:$('#Core', dom).get(),
                _percentageKeysArea:$('#PercentageKeysArea', dom).get(),
            });
            var that = this;

            $(this._recommendationType).change(function() {
                if ($(this).val() == 'percentage') {
                    $(that._percentageKeys).addClass('validate').change();
                    $(that._percentageKeysArea).removeClass('hidden');
                } else {
                    $(that._percentageKeys).removeClass('validate').change();
                    $(that._percentageKeysArea).addClass('hidden');
                }
            });

            $.data($(this._core)[0], 'pct', {
                getXML:function() {
                    var config = '<Recommendation>';
                    config += '<Hint>' + $(that._hint).val() + '</Hint>';
                    config += '<RecommendationType>' + $(that._recommendationType).val() + '</RecommendationType>';
                    config += '<Key>' + $(that._key).val() + '</Key>';
                    config += '<Name>' + $(that._name).val() + '</Name>';
                    config += '<PercentageKeys>' + $(that._percentageKeys).val() + '</PercentageKeys>';
                    config += '<MDValue>' + $(that._mdValue).val() + '</MDValue>';
                    config += '<ReferenceKey>' + $(that._referenceKey).val() + '</ReferenceKey>';
                    config += '</Recommendation>';
                    return config;
                }
            });
            $(this._remove).click(function() {
                if (confirm('Remove recommendation?')) {
                    if (confirm('Removing recommendation can result in broken backward compatibility. Remove recommendation?')) {
                        $(that._body).remove();
                    }
                }
            });
            $(this._key).val(PCT.randomString(15)).change();
            $(this._clone).click(function() {
                $.data($(that._core).parents('.divModelRecommendations')[0], 'pct').cloneTree($.parseXML('<root>' + $.data($(that._core)[0], 'pct').getXML() + '</root>'));
            });
            $(this._name).change(function() {
                $(that._recommendationTitle).html($(this).val());
            });
            $(this._name).val('').change();
            $(this._recommendationTitle).click(function() {
                $(that._remove).toggleClass('hidden');
                $(that._settingsPane).toggleClass('hidden');
            });
            $.extend(this, PCT.base, {
                root:$('<div></div>').append(dom.contents()),
                getHead:function() {
                    return this._head;
                },
                init:function(initialData) {
                    $(this._hint).val($('>Hint', initialData).text()).change();
                    $(this._key).val('').val($('>Key', initialData).text()).change();
                    $(this._recommendationType).val($('>RecommendationType', initialData).text()).change();
                    $(this._name).val($('>Name', initialData).text()).change();
                    $(this._percentageKeys).val($('>PercentageKeys', initialData).text()).change();
                    $(this._mdValue).val($('>MDValue', initialData).text()).change();
                    $(this._referenceKey).val($('>ReferenceKey', initialData).text()).change();
                    $(this._remove).addClass('hidden');
                    $(this._settingsPane).addClass('hidden');
                    var that = this;
                    return this;
                }
            });
        },
        currencyRegion:function(dom) {
            if (!dom) {
                dom = PCT.getTemplate('currencyRegion');
            }
            dom = $('<div></div>').append(dom);
            $.extend(this, {
                _head:$(dom).children().first().get(),
                _body:$(dom).contents(),
                _key:$('#Key', dom).get(),
                _currencyRegionTitle:$('#Title', dom).get(),
                _settings:$('#Settings', dom).get(),
                _settingsPane:$('#SettingsPane', dom).get(),
                _remove:$('#Remove', dom).get(),
                _core:$('#Core', dom).get()
            });
            var that = this;
            $.data($(this._core)[0], 'pct', {
                getXML:function() {
                    var config = '<Region>';
                    config += '<Key>' + $(that._key).val() + '</Key>';
                    config += '</Region>';
                    return config;
                }
            });
            $(this._remove).click(function() {
                if (confirm('Remove region?')) {
                    $(that._body).remove();
                }
            });
            $(this._key).change(function() {
                $(that._currencyRegionTitle).html($(this).val());
            });
            $(this._currencyRegionTitle).click(function() {
                $(that._remove).toggleClass('hidden');
                $(that._settingsPane).toggleClass('hidden');
            });
            $.extend(this, PCT.base, {
                root:$('<div></div>').append(dom.contents()),
                getHead:function() {
                    return this._head;
                },
                init:function(initialData) {
                    $(this._key).val($('>Key', initialData).text()).change();
                    $(this._settingsPane).addClass('hidden');
                    $(this._remove).addClass('hidden');
                    return this;
                }
            });
        },
        user:function(dom) {
            if (!dom) {
                dom = PCT.getTemplate('user');
            }
            dom = $('<div></div>').append(dom);
            $.extend(this, {
                _head:$(dom).children().first().get(),
                _body:$(dom).contents(),
                _cn:$('#CN', dom).get(),
                _userTitle:$('#Title', dom).get(),
                _addUserRegion:$('#AddUserRegion', dom).get(),
                _addUserAuthLevel:$('#AddUserAuthLevel', dom).get(),
                _settings:$('#Settings', dom).get(),
                _settingsPane:$('#SettingsPane', dom).get(),
                _userRegions:$('#UserRegions', dom).get(),
                _userAuthLevels:$('#UserAuthLevels', dom).get(),
                _expand:$('#Expand', dom).get(),
                _remove:$('#Remove', dom).get(),
                _maxProductDiscount:$('#MaxProductDiscount', dom).get(),
                _maxSupportDiscount:$('#MaxSupportDiscount', dom).get(),
                _mdrDiscount:$('#MDRDiscount', dom).get(),
                _psDiscount:$('#PSDiscount', dom).get(),
                _trainingDiscount:$('#TrainingDiscount', dom).get(),
                _name:$('#Name', dom).get(),
                _email:$('#Email', dom).get(),
                _clone:$('#Clone', dom).get(),
                _deprecated:$('#Deprecated', dom).get(),
                _admin:$('#Admin', dom).get(),
                _salesSupport:$('#SalesSupport', dom).get(),
                _core:$('#Core', dom).get()
            });
            var that = this;
            $(this._userRegions, dom).sortable({
                revert:true,
                handle: '.userRegionDrag',
                connectWith: '.divUserRegions'
            });
            $(this._userAuthLevels, dom).sortable({
                revert:true,
                handle: '.userAuthLevelDrag',
                connectWith: '.divUserAuthLevels'
            });
            $.data($(this._core)[0], 'pct', {
                getXML:function() {
                    var config = '<User>';
                    config += '<CN>' + $(that._cn).val() + '</CN>';
                    config += '<MaxProductDiscount>' + $(that._maxProductDiscount).val() + '</MaxProductDiscount>';
                    config += '<MaxSupportDiscount>' + $(that._maxSupportDiscount).val() + '</MaxSupportDiscount>';
                    config += '<MDRDiscount>' + $(that._mdrDiscount).val() + '</MDRDiscount>';
                    config += '<PSDiscount>' + $(that._psDiscount).val() + '</PSDiscount>';
                    config += '<TrainingDiscount>' + $(that._trainingDiscount).val() + '</TrainingDiscount>';
                    config += '<Name>' + $(that._name).val() + '</Name>';
                    config += '<Email>' + $(that._email).val() + '</Email>';
                    config += '<Deprecated>' + ($(that._deprecated).prop('checked') ? 'true' : 'false') + '</Deprecated>';
                    config += '<Admin>' + ($(that._admin).prop('checked') ? 'true' : 'false') + '</Admin>';
                    config += '<SalesSupport>' + ($(that._salesSupport).prop('checked') ? 'true' : 'false') + '</SalesSupport>';
                    config += '<Regions>';
                    $(that._userRegions).children().each(function() {
                        if ($(this).hasClass('divUserRegion'))
                            config += $.data($(this)[0], 'pct').getXML();
                    });
                    config += '</Regions>';
                    config += '<Levels>';
                    $(that._userAuthLevels).children().each(function() {
                        if ($(this).hasClass('divUserAuthLevel'))
                            config += $.data($(this)[0], 'pct').getXML();
                    });
                    config += '</Levels>';
                    config += '</User>';
                    return config;
                }
            });
            $(this._remove).click(function() {
                if (confirm('Remove user?')) {
                    //if (confirm('Removing currency can result in broken backward compatibility. Remove currency?')) {
                    $(that._body).remove();
                    //}
                }
            });
            $(this._deprecated).change(function() {
                if ($(this).prop('checked')) {
                    $(that._userTitle).addClass('deprecated');
                } else {
                    $(that._userTitle).removeClass('deprecated');
                }
            });
            $(this._admin).change(function() {
                if ($(this).prop('checked')) {
                    $(that._userTitle).addClass('admin');
                } else {
                    $(that._userTitle).removeClass('admin');
                }
            });
            $(this._clone).click(function() {
                $.data($(that._core).parents('.divModelUsers')[0], 'pct').cloneTree($.parseXML('<root>' + $.data($(that._core)[0], 'pct').getXML() + '</root>'));
            });
            $(this._cn).change(function() {
                $(that._userTitle).html($(this).val());
            });
            $(this._cn).val('').change();

            $(this._expand).click(function(arg) {
                $(that._userRegions).toggleClass('hidden');
                $(that._userAuthLevels).toggleClass('hidden');
                if ($(that._userRegions).hasClass('hidden')) {
                    $(that._expand).html('Expand');
                } else {
                    $(that._expand).html('Collapse');
                }
            });
            $(this._addUserRegion).click(function() {
                if ($(that._userRegions).hasClass('hidden')) {
                    $(that._userRegions).removeClass('hidden');
                    $(that._userAuthLevels).removeClass('hidden');
                    $(that._expand).html('Collapse');
                }
                that.addUserRegion();
            });
            $(this._addUserAuthLevel).click(function() {
                if ($(that._userAuthLevels).hasClass('hidden')) {
                    $(that._userRegions).removeClass('hidden');
                    $(that._userAuthLevels).removeClass('hidden');
                    $(that._expand).html('Collapse');
                }
                that.addUserAuthLevel();
            });
            $(this._userTitle).click(function() {
                $(that._remove).toggleClass('hidden');
                $(that._settingsPane).toggleClass('hidden');
            });
            $.extend(this, PCT.base, {
                root:$('<div></div>').append(dom.contents()),
                getHead:function() {
                    return this._head;
                },
                init:function(initialData) {
                    $(this._cn).val($('>CN', initialData).text()).change();
                    $(this._maxProductDiscount).val($('>MaxProductDiscount', initialData).text()).change();
                    $(this._maxSupportDiscount).val($('>MaxSupportDiscount', initialData).text()).change();
                    $(this._mdrDiscount).val($('>MDRDiscount', initialData).text()).change();
                    $(this._psDiscount).val($('>PSDiscount', initialData).text()).change();
                    $(this._trainingDiscount).val($('>TrainingDiscount', initialData).text()).change();
                    $(this._name).val($('>Name', initialData).text()).change();
                    $(this._email).val($('>Email', initialData).text()).change();
                    $(this._settingsPane).addClass('hidden');
                    $(this._userRegions).addClass('hidden');
                    $(this._userAuthLevels).addClass('hidden');
                    $(this._deprecated).prop('checked', ($('>Deprecated', initialData).text() == 'true')).change();
                    $(this._admin).prop('checked', ($('>Admin', initialData).text() == 'true')).change();
                    $(this._salesSupport).prop('checked', ($('>SalesSupport', initialData).text() == 'true')).change();
                    $(this._expand).html('Expand');
                    $(this._remove).addClass('hidden');
                    var that = this;
                    $('>Regions>Region', initialData).each(function() {
                        that.addUserRegion((new PCT.userRegion()).setRoot(that._userRegions).init(this));
                    });
                    $('>Levels>Level', initialData).each(function() {
                        that.addUserAuthLevel((new PCT.userAuthLevel()).setRoot(that._userAuthLevels).init(this));
                    });
                    return this;
                },
                addUserRegion:function(region) {
                    if (region) {
                        $('html, body').animate({
                            scrollTop: $(region.getHead()).offset().top
                        }, 000);
                    } else {
                        this.addUserRegion((new PCT.userRegion()).setRoot(this._userRegions));
                    }
                },
                addUserAuthLevel:function(level) {
                    if (level) {
                        $('html, body').animate({
                            scrollTop: $(level.getHead()).offset().top
                        }, 000);
                    } else {
                        this.addUserAuthLevel((new PCT.userAuthLevel()).setRoot(this._userAuthLevels));
                    }
                }
            });
        },
        userRegion:function(dom) {
            if (!dom) {
                dom = PCT.getTemplate('userRegion');
            }
            dom = $('<div></div>').append(dom);
            $.extend(this, {
                _head:$(dom).children().first().get(),
                _body:$(dom).contents(),
                _key:$('#Key', dom).get(),
                _userRegionTitle:$('#Title', dom).get(),
                _settings:$('#Settings', dom).get(),
                _settingsPane:$('#SettingsPane', dom).get(),
                _remove:$('#Remove', dom).get(),
                _core:$('#Core', dom).get()
            });
            var that = this;
            $.data($(this._core)[0], 'pct', {
                getXML:function() {
                    var config = '<Region>';
                    config += '<Key>' + $(that._key).val() + '</Key>';
                    config += '</Region>';
                    return config;
                }
            });
            $(this._remove).click(function() {
                if (confirm('Remove region?')) {
                    $(that._body).remove();
                }
            });
            $(this._key).change(function() {
                $(that._userRegionTitle).html($(this).val());
            });
            $(this._userRegionTitle).click(function() {
                $(that._remove).toggleClass('hidden');
                $(that._settingsPane).toggleClass('hidden');
            });
            $.extend(this, PCT.base, {
                root:$('<div></div>').append(dom.contents()),
                getHead:function() {
                    return this._head;
                },
                init:function(initialData) {
                    $(this._key).val($('>Key', initialData).text()).change();
                    $(this._settingsPane).addClass('hidden');
                    $(this._remove).addClass('hidden');
                    return this;
                }
            });
        },
        userAuthLevel:function(dom) {
            if (!dom) {
                dom = PCT.getTemplate('userAuthLevel');
            }
            dom = $('<div></div>').append(dom);
            $.extend(this, {
                _head:$(dom).children().first().get(),
                _body:$(dom).contents(),
                _key:$('#Key', dom).get(),
                _subkey:$('#SubKey', dom).get(),
                _userAuthLevelTitle:$('#Title', dom).get(),
                _settings:$('#Settings', dom).get(),
                _settingsPane:$('#SettingsPane', dom).get(),
                _remove:$('#Remove', dom).get(),
                _core:$('#Core', dom).get()
            });
            var that = this;
            $.data($(this._core)[0], 'pct', {
                getXML:function() {
                    var config = '<Level>';
                    config += '<Key>' + $(that._key).val() + '</Key>';
                    config += '<SubKey>' + $(that._subkey).val() + '</SubKey>';
                    config += '</Level>';
                    return config;
                }
            });
            $(this._remove).click(function() {
                if (confirm('Remove authority level?')) {
                    $(that._body).remove();
                }
            });
            $(this._key).change(function() {
                $(that._userAuthLevelTitle).html($(this).val() + ' = ' + $(that._subkey).val());
            });
            $(this._subkey).change(function() {
                $(that._userAuthLevelTitle).html($(that._key).val() + ' = ' + $(this).val());
            });
            $(this._userAuthLevelTitle).click(function() {
                $(that._remove).toggleClass('hidden');
                $(that._settingsPane).toggleClass('hidden');
            });
            $.extend(this, PCT.base, {
                root:$('<div></div>').append(dom.contents()),
                getHead:function() {
                    return this._head;
                },
                init:function(initialData) {
                    $(this._key).val($('>Key', initialData).text()).change();
                    $(this._subkey).val($('>SubKey', initialData).text()).change();
                    $(this._settingsPane).addClass('hidden');
                    $(this._remove).addClass('hidden');
                    return this;
                }
            });
        },
        file:function(dom) {
            if (!dom) {
                dom = PCT.getTemplate('file');
            }
            dom = $('<div></div>').append(dom);
            $.extend(this, {
                _head:$(dom).children().first().get(),
                _body:$(dom).contents(),
                _fileTitle:$('#Title', dom).get(),
                _settings:$('#Settings', dom).get(),
                _settingsPane:$('#SettingsPane', dom).get(),
                _remove:$('#Remove', dom).get(),
                _name:$('#Name', dom).get(),
                _hint:$('#Hint', dom).get(),
                _key:$('#Key', dom).get(),
                _fileName:$('#FileName', dom).get(),
                /*_fileRow:$('#FileRow', dom).get(),*/
                _core:$('#Core', dom).get()
            });
            var that = this;
            $.data($(this._core)[0], 'pct', {
                getXML:function() {
                    var config = '<File>';
                    config += '<Name>' + $(that._name).val() + '</Name>';
                    config += '<Hint>' + $(that._hint).val() + '</Hint>';
                    config += '<Key>' + $(that._key).val() + '</Key>';
                    config += '</File>';
                    return config;
                }
            });
            $(this._name).change(function() {
                $(that._fileTitle).html($(this).val());
            });
            $(this._key).change(function() {
                $(that._fileName).html($(this).val());
            });
            $(this._remove).click(function() {
                if (confirm('Remove file?')) {
                    //if (confirm('Removing currency can result in broken backward compatibility. Remove currency?')) {
                    $(that._body).remove();
                    //}
                }
            });
            $(this._fileTitle).click(function() {
                $(that._remove).toggleClass('hidden');
                $(that._settingsPane).toggleClass('hidden');
            });
            $.extend(this, PCT.base, {
                root:$('<div></div>').append(dom.contents()),
                getHead:function() {
                    return this._head;
                },
                init:function(initialData) {
                    $(this._name).val($('>Name', initialData).text()).change();
                    $(this._key).val($('>Key', initialData).text()).change();
                    $(this._hint).val($('>Hint', initialData).text()).change();
                    $(this._settingsPane).addClass('hidden');
                    $(this._remove).addClass('hidden');
                    //$(this._fileRow).remove();
                    var that = this;
                    return this;
                }
            });
        },
        license:function(dom) {
            if (!dom) {
                dom = PCT.getTemplate('license');
            }
            dom = $('<div></div>').append(dom);
            $.extend(this, {
                _head:$(dom).children().first().get(),
                _body:$(dom).contents(),
                _name:$('#Name', dom).get(),
                _licenseTitle:$('#Title', dom).get(),
                _key:$('#Key', dom).get(),
                _settings:$('#Settings', dom).get(),
                _settingsPane:$('#SettingsPane', dom).get(),
                _remove:$('#Remove', dom).get(),
                _clone:$('#Clone', dom).get(),
                _core:$('#Core', dom).get()
            });
            var that = this;
            $.data($(this._core)[0], 'pct', {
                getXML:function() {
                    var config = '<License>';
                    config += '<Name>' + $(that._name).val() + '</Name>';
                    config += '<Key>' + $(that._key).val() + '</Key>';
                    config += '</License>';
                    return config;
                }
            });
            $(this._clone).click(function() {
                $.data($(that._core).parents('.divProductLicenses')[0], 'pct').cloneTree($.parseXML('<root>' + $.data($(that._core)[0], 'pct').getXML() + '</root>'));
            });
            $(this._remove).click(function() {
                if (confirm('Remove licensing model?')) {
                    if (confirm('Removing licensing model can result in broken backward compatibility. Remove licensing model?')) {
                        $(that._body).remove();
                    }
                }
            });
            $(this._name).change(function() {
                $(that._licenseTitle).html($(this).val());
            });
            $(this._key).val(PCT.randomString(15)).change();
            $(this._licenseTitle).click(function() {
                $(that._remove).toggleClass('hidden');
                $(that._settingsPane).toggleClass('hidden');
            });
            $.extend(this, PCT.base, {
                root:$('<div></div>').append(dom.contents()),
                getHead:function() {
                    return this._head;
                },
                init:function(initialData) {
                    $(this._name).val($('>Name', initialData).text()).change();
                    $(this._key).val('').val($('>Key', initialData).text()).change();
                    $(this._settingsPane).addClass('hidden');
                    $(this._remove).addClass('hidden');
                    return this;
                }
            });
        }
    });
})(jQuery, window, qq);
