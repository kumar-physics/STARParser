// Change to modify behavior
var skip_empty_loops = false;

// Don't change these yourself
var star;
var to_process = "";
var token = "";

function validateTag(tag, value){

    // If the tag isn't in the dict it is invalid
    if (tags[tag] == null){
        return "nodict";
    }

    // Get the tag dict
    dict = tags[tag];

    // Null tag is not allowed
    if (((value == null) || ( value == "") || ( value == ".") || ( value == "?"))
            && (dict["null_valid"] == false)){
        return "illegalnull";
    }
    // Null is valid, so don't check the type if the type is null
    else {
        if ((value == ".") || (value == "?")){
            return "valid";
        }
    }

    our_type = null;
    // Check the data type
    if(Number(value) == parseInt(value)) {
        // This prevents 27.000 from being interpreted as an INT
        if (value.indexOf(".") != -1){
            our_type = "FLOAT";
        }
        else {
            our_type = "INTEGER";
        }
    }
    else if(Number(value) == parseFloat(value)) {
        our_type = "FLOAT";
    }

    // Check for too long if string
    if ((dict['type'] == "VARCHAR") || (dict['type'] == "CHAR")){
        if (value.length > dict['length']){
            return "warn";
        }
    }
    // Check it if integer
    else if (dict['type'] == "INTEGER"){
        if (our_type != "INTEGER"){
            return "warn";
        }
    }
    // Check if float
    else if (dict['type'] == "FLOAT"){
        if ((our_type != "FLOAT") && (our_type != "INTEGER")){
            return "warn";
        }
    // Check if date
    } else if (dict['type'] == "DATETIME year to day"){
        pattern = new RegExp("^[0-9]{4}-[0-9]{2}-[0-9]{2}$");
        if (pattern.test(value) != true){
            return "warn";
        }
    } else {
        // We don't know the type so we can't validate
        return "";
    }

    // It appears valid
    return "";
}

function getTitle(tag){
    if (tags[tag] == null){
        return "Tag not present in dictionary.";
    } else {
        return tags[tag]['description'];
    }
}

function getType(tag){
    if (tags[tag] == null){
        return "Tag not present in dictionary.";
    } else {
        not_null = "";
        if (!tags[tag]['null_valid']){
            not_null = " NOT NULL.";
        }

        if (tags[tag]['length'] != null){
            return tags[tag]['type'] + " with length " + tags[tag]['length'] + not_null;
        } else {
            return tags[tag]['type'] + not_null;
        }
    }
}

// Validate a tag and update it's classes
function updateTag(tag, value){
    document.getElementById(tag).className = "line " + validateTag(tag, value);
}

// Validate a loop datum and update its tags
function updateDatum(tag, id, value){
    document.getElementById(id).className = "editable " + validateTag(tag, value);
}

// DOM creation methods
function createEditableSpan(content, link_to){
    content = content.replace(/\n/g, "⏎");
    span = $("<span></span>").addClass("editable").html(content).attr('contentEditable', true)
    if (link_to != null){ span.attr("onblur", "this.innerHTML = this.innerHTML.replace(/<br>/g,'⏎'); " + link_to + " = this.innerHTML;"); }
    return span;
}

function createUneditableSpan(content){
    return $("<span></span>").addClass("uneditable").html(content);
}

function createLineDiv(){
    return $("<div></div>").addClass("line highlight");
}

// Create a loop tag
function createLoopColumn(name){
    tag_div = createLineDiv().attr("id", name).addClass("looptag");
    tag_name = createUneditableSpan(name).addClass("twoindent").prop("title", getTitle(name));
    tag_div.append(tag_name);
    return tag_div;
}

function toggleButtonHandler(img_button, id){
    if (img_button.src.indexOf("minimize.png") != -1){
        img_button.src = "maximize.png";
        $("#"+id).hide();
    } else {
        img_button.src = "minimize.png";
        $("#"+id).show();
    }
}

/* NMR-STAR */

// NMR-STAR definition
var NMRSTAR = function (name) {
    this.dataname = name;
    this.saveframes = [];
};

NMRSTAR.prototype.toHTML = function() {
    root = $("#dynamic_anchor").empty().append(createLineDiv().append(createUneditableSpan("data_" + star.dataname))).addClass("star");
    for (s=0; s < star.saveframes.length; s++){
        root.append(star.saveframes[s].toHTML(s));
    }
    return root;
}

NMRSTAR.prototype.print = function() {
    result =  "data_" + this.dataname + "\n\n";

    this.saveframes.forEach(function(saveframe) {
        result += saveframe.print() + "\n";
    });

    return result;
};

NMRSTAR.prototype.addSaveframe = function(saveframe){
    this.saveframes.push(saveframe);
}

NMRSTAR.prototype.download = function(){
    download(this.dataname + "_3.str" , this.print());
}

// Saveframe definition
var SAVEFRAME = function (name) {
    this.name = name;
    this.tag_prefix = "";
    this.tags = [];
    this.loops = [];
};

SAVEFRAME.prototype.toHTML = function(saveframe_ordinal){

    outer_saveframe_div = $("<div><div>");
    // Create the shrink button
    shrink = $('<img name="minimize" src="minimize.png" title="' + this.name + '">');
    shrink.attr('onclick', 'toggleButtonHandler(this, "saveframe_' + saveframe_ordinal + '");');
    outer_saveframe_div.append(shrink);

    // Create the encapsulating <div>
    saveframe_div = $("<div><div>").attr("id", "saveframe_" + saveframe_ordinal);

    // Add the save_name line
    saveframe_div.append(createLineDiv().append(
        createUneditableSpan("save_"),
        createEditableSpan(this.name, "star.saveframes[" + saveframe_ordinal + "].name")));

    // Add the tags
    tag_table = $("<table></table>").addClass("oneindent").css("maxWidth", "95%");
    for (t=0; t < this.tags.length; t++){

        // Tag values
        name = this.tag_prefix + '.' + this.tags[t][0];
        value = this.tags[t][1];
        link_to = sprintf("star.saveframes[%d].tags[%d][1]", saveframe_ordinal, t);

        // Create the row
        table_row = $("<tr></tr>").attr("id", name).addClass(validateTag(name, value)).addClass("highlight");

        // Create the tag td
        tag_td = $("<td></td>").append(createUneditableSpan(name).prop("title", getTitle(name)));

        // Create the value td
        value_span = createEditableSpan(value, link_to).prop("title", getType(name));
        value_span.attr("onblur", sprintf("%s updateTag('%s', this.innerHTML);", value_span.attr("onblur"), name));
        value_td = $("<td></td>").append(value_span);

        table_row.append(tag_td, value_td);
        tag_table.append(table_row);
    }
    saveframe_div.append(tag_table);

    // Add the loops
    for (ll=0; ll < this.loops.length; ll++){
        saveframe_div.append(this.loops[ll].toHTML(saveframe_ordinal, ll));
    }

    // Add the "save_"
    saveframe_div.append(createLineDiv().append(createUneditableSpan("save_")));

    outer_saveframe_div.append(saveframe_div);
    return outer_saveframe_div;
}

SAVEFRAME.prototype.print = function() {

    width = 0;

    this.tags.forEach(function(tag) {
        if (tag[0].length > width){
            width = tag[0].length;
        }
    });
    width += this.tag_prefix.length + 2;

    // Print the saveframe
    ret_string = sprintf("save_%s\n", this.name)
    pstring = sprintf("   %%-%ds  %%s\n", width)
    mstring = sprintf("   %%-%ds\n;\n%%s;\n", width)

    tag_prefix = this.tag_prefix;

    this.tags.forEach(function(tag){
        cleaned_tag = cleanValue(tag[1]);

        if (cleaned_tag.indexOf("\n") == -1){
            ret_string +=  sprintf(pstring, this.tag_prefix + "." + tag[0], cleaned_tag)
        } else {
            ret_string +=  sprintf(mstring, this.tag_prefix + "." + tag[0], cleaned_tag)
        }
    });

    this.loops.forEach(function(loop) {
        ret_string += loop.print();
    });

    return ret_string + "save_\n";
};

SAVEFRAME.prototype.addTag = function(tag, value){
    this.tag_prefix = tag.substring(0, tag.indexOf("."));
    this.tags.push([tag.substring(tag.indexOf(".")+1),value]);
}

SAVEFRAME.prototype.addLoop = function(loop){
    this.loops.push(loop);
}

// Loop definition
var LOOP = function (category) {
    this.columns = [];
    this.data = [];
    this.category = category;
};

LOOP.prototype.toHTML = function(saveframe_ordinal, loop_ordinal){

    loop_id = sprintf("saveframe_%d_loop_%d", saveframe_ordinal, loop_ordinal);

    outer_loop_div = $("<div><div>");
    // Create the shrink button
    shrink = $('<img name="minimize" src="minimize.png" title="' + this.category + '">');
    shrink.attr('onclick', 'toggleButtonHandler(this, "' + loop_id + '");');
    outer_loop_div.append(shrink);

    loop_div = $("<div><div>").attr("id", loop_id);
    outer_loop_div.append(loop_div);

    loop_div.append(createLineDiv().append(createUneditableSpan("loop_")).addClass("oneindent"));

    // Add the loop columns
    for (l=0; l < this.columns.length; l++){
        loop_div.append(createLoopColumn(this.category + '.' + this.columns[l]));
    }

    // With a table
    table = $("<table></table>").addClass("twoindent alternatingcolor").css("maxWidth", "95%");
    for (d=0; d < this.data.length; d++){
        the_row = $("<tr></tr>");

        for (n=0; n < this.data[d].length; n++){
            tag_name = this.category + "." + this.columns[n];
            our_id = sprintf("star.saveframes[%d].loops[%d].data[%d][%d]", saveframe_ordinal, loop_ordinal, d, n);
            datum = createEditableSpan(this.data[d][n], our_id).addClass("highlight");
            datum.addClass(validateTag(tag_name, this.data[d][n]));
            datum.prop("title", getTitle(tag_name));
            datum.attr("onblur", sprintf("%s updateDatum('%s', '%s', this.innerHTML);", datum.attr("onblur"), tag_name, our_id));
            datum.attr("id", our_id);
            the_td = $("<td></td>").append(datum);
            the_row.append(the_td);
        }
        table.append(the_row);
    }
    loop_div.append(table);

    loop_div.append(createLineDiv().append(createUneditableSpan("stop_")).addClass("oneindent"));
    return outer_loop_div
}

LOOP.prototype.addColumn = function(column_name){
    column = column_name.substring(column_name.indexOf(".")+1)
    category = column_name.substring(0,column_name.indexOf("."))
    if ((this.category != undefined) && (this.category != category)){
        throw "Error - mismatching columns: " + this.category + " " + category;
    }
    this.category = category;
    if (this.columns.indexOf(column) == -1){
        this.columns.push(column);
    }
}

// Just add a data bit to the next appropriate place
LOOP.prototype.addDatum = function(value){

    // Make sure the columns are defined
    if (this.columns.length == 0){
        throw "Cannot add data to a loop without columns using this method. Please make sure to specify the columns using addColumn() first.";
    }

    // Create the first row if neccessary
    if (this.data.length == 0){
        this.data.push([]);
    }

    // Add the data
    last_row = this.data[this.data.length-1];
    // We need a new row
    if (last_row.length == this.columns.length){
        this.data.push([]);
        last_row = this.data[this.data.length-1];
    }
    last_row.push(value);
}

LOOP.prototype.print = function() {

    // Check for empty loops
    if (this.data.length == 0){
        if (skip_empty_loops){
            return "";
        } else if (this.columns.length == 0){
            return "\n   loop_\n\n   stop_\n";
        }
    }

    // Can't print data without columns
    if (this.columns.length == 0){
        throw sprintf("Impossible to print data if there are no associated tags. Loop: '%s'.", self.category);
    }

    // Make sure that if there is data, it is the same width as the column tags
    if (this.data.length > 0){
        for (var n=0; n < this.data.length; n++){
            if (this.columns.length != this.data[n].length){
                throw sprintf("The number of column tags must match width of the data. Row: %d Loop: '%s'.", n, this.category);
            }
        }
    }

    // Start the loop
    ret_string = "\n   loop_\n";
    // Print the columns
    pstring = "      %-s\n";

    // Check to make sure our category is set
    if (this.category == undefined){
        throw "The category was never set for this loop. Either add a column with the category intact, specify it when generating the loop, or set it using setCategory.";
    }

    // Print the categories
    loop_category = this.category;
    this.columns.forEach(function(column){
        ret_string += sprintf(pstring, loop_category + "." + column);
    });

    ret_string += "\n";

    // If there is data to print, print it
    if (this.data.length != 0){

        widths = Array(this.data[0].length).fill(0);

        // Figure out the maximum row lengths
        this.data.forEach(function(row){
            for (var n=0; n < row.length; n++){
                // Don't count data that goes on its own line
                if (row[n].indexOf("\n") != -1){
                    continue;
                }
                if (row[n].length + 3 > widths[n]){
                    widths[n] = row[n].length + 3;
                }
            }
        });

        // Go through and print the data
        this.data.forEach(function(row){

            // Each row starts with whitespace
            ret_string += "     ";

            // Get the data ready for printing
            for (var n=0; n < row.length; n++){

                datum_copy = cleanValue(row[n]);
                if (datum_copy.indexOf("\n") != -1){
                    datum_copy = sprintf("\n;\n%s;\n", datum_copy);
                }

                // Add the data to the return string
                ret_string += sprintf("%-" + widths[n] + "s", datum_copy);
            }

            // End the row
            ret_string += " \n";
        });
    }

    // Close the loop
    ret_string += "   stop_\n";
    return ret_string
};

/*Automatically quotes the value in the appropriate way. Don't quote values you send to this method or they will show up in another set of quotes as part of the actual data. E.g.:

cleanValue('"e. coli"') returns '\'"e. coli"\''

while

cleanValue("e. coli") returns "'e. coli'"

This will automatically be called on all values when you use a str() method (so don't call it before inserting values into tags or loops).

Be mindful of the value of str_conversion_dict as it will effect the way the value is converted to a string.*/
function cleanValue(value) {

    // If the user inserts a newline in the web editor replace it with a newline
    value = value.replace(/<br>/g, "\n");
    value = value.replace(/⏎/g, "\n");

    // Values that go on their own line don't need to be touched
    if (value.indexOf("\n") != -1){
        if (value.substring(value.length-1) != "\n"){
            return value + "\n";
        } else {
            return value
        }
    }

    // No empty values
    if (value == undefined){
        throw "Empty strings are not allowed as values. Use a '.' or a '?' if needed.";
    }

    // Normally we wouldn't autoconvert null values for them but it may be appropriate here
    if (value == ""){
        value = ".";
    }

    if ((value.indexOf(" ") != -1) || (value.indexOf("\t") != -1) || (value.indexOf("#") != -1) || (value.startsWith("_")) || ((value.length > 4) && (value.startsWith("data_")) || (value.startsWith("save_")) || (value.startsWith("loop_")) || (value.startsWith("stop_")))) {

        if (value.indexOf('"') != -1){
            return sprintf("'%s'", value);
        } else if (value.indexOf("'") != -1){
            return sprintf('"%s"', value);
        } else if ((value.indexOf('"') != -1) && (value.indexOf("'") != -1)) {
            return sprintf('%s\n', value);
        } else {
            return sprintf("'%s'", value);
        }
    }

    return value
}



/*
 *
 *
 *  Parser methods
 *
 *
 */

// Returns the next block of text to be processed as well as the remaining text to process
function getToken(){

    // Nothing left
    if (to_process == null){
        token = null;
        return token;
    }

    // Tokenize

    //Trim
    tmp = to_process.trim();

    // Handle comments
    if (tmp.startsWith("#")){
        // At the last line
        if (tmp.indexOf("\n") == -1){
            token = tmp;
            to_process = null;
            return token;
        }
        // Any other line
        token = tmp.substring(0,tmp.indexOf("\n"));
        to_process = tmp.substring(tmp.indexOf("\n")+1);
        return token;
    }

    // Handle multi-line values
    if (tmp.startsWith(";\n")){
        tmp = tmp.substring(2);
        // Search for end of multi-line value
        until = tmp.indexOf(";\n")
        if (until == -1){
            throw "Invalid file. Multi-line comment never ends. Multi-line comments must terminate with a line that consists ONLY of a ';' without characters before or after. (Other than the newline.";
        } else {
            token = tmp.substring(0,until);
            to_process = tmp.substring(until+1);
            return token;
        }
    }

    // Handle values quoted with '
    if (tmp.startsWith("'")){
        until = tmp.indexOf("'",1);
        if (until == -1){
            throw "Invalid file. ' quoted value was never terminated.";
        } else {
            token = tmp.substring(1,until);
            to_process = tmp.substring(until+1);
            return token;
        }
    }

    // Handle values quoted with "
    if (tmp.startsWith('"')){
        until = tmp.indexOf('"',1);
        if (until == -1){
            throw "Invalid file. ' quoted value was never terminated.";
        } else {
            token = tmp.substring(1,until);
            to_process = tmp.substring(until+1);
            return token;
        }
    }

    // Split on whitespace
    tmp = to_process.trim().match(/^(\S+)\s+([\S\s]*)/);

    // Check if the search failed and if so return last token
    if (tmp == null){
        token = to_process.trim();
        to_process = null;
        return token;
    }

    // Simple tokenizing
    tmp = tmp.slice(1);
    token = tmp[0];
    to_process = tmp[1];
    return token;
}

// Parse a NMR-STAR file but catch parse exceptions and display them
function starCatcher(star){
    try {
        return parseSTAR(star);
    } catch (err){
        $("#parser_messages").html("<font color='red'>Parse error: " + err + "</font>");
    }
}

// Parse a STAR file - return NMRSTAR object
function parseSTAR(star){

    to_process = star;

    // First get the data block
    getToken();
    // Make sure this is actually a STAR file
    if (!token.startsWith("data_")){ throw "Invalid file. NMR-STAR files must start with 'data_'"; }
    // Make sure there is a data name
    if (token.length < 6){ throw "data_ must be followed by data name."; }

    // Create the NMRSTAR object
    mystar = new NMRSTAR(token.substring(5))
    curframe = null;
    curloop = null;
    state = "star";

    sanity = 0;

    // Parse all the saveframes
    do {
        // See if we are done and get the next token
        if (getToken() == null){ break; }

        // Drop comments
        if (token.startsWith("#")){ continue;}

        // We are expecting to find a saveframe
        if (state == "star"){
            if (!token.startsWith("save_")){ throw "Only save_ is valid here. Found " + token; }
            if (token.length < 6){ throw "save_ must be followed by saveframe name."; }
            state = "saveframe";
            curframe = new SAVEFRAME(token.substring(5));
            mystar.addSaveframe(curframe);
        }

        // We are in a saveframe
        else if (state == "saveframe"){
            if (token == "loop_"){
                state = "loop";
                curloop = new LOOP();
                curframe.addLoop(curloop);
            }
            // Close saveframe
            else if (token == "save_"){
                curframe = null;
                state = "star";
            }
            // Invalid content in saveframe
            else if (!token.startsWith("_")){
                throw "Invalid token found in saveframe: " + token;
            }
            // Add a tag
            else {
                tag_name = token;
                getToken();
                curframe.addTag(tag_name, token);
            }
        }

        // We are in a loop
        else if (state == "loop"){
            // Add a column
            if (token.startsWith("_")){
                curloop.addColumn(token);
            }
            // On to data
            else {
                curloop.addDatum(token);
                state = "data";
            }
        }

        // We are in the data block of a loop
        else if (state == "data"){
            if (token == "stop_"){
                curloop = null;
                state = "saveframe";
            } else {
                curloop.addDatum(token);
            }
        }
    } while (true);

    return mystar;
}

function openFile() {
    var input = document.getElementById("nmrstar_file");

    $("#parser_messages").html("");
    $("#dynamic_anchor").empty().removeClass("star");

    var reader = new FileReader();
    reader.onload = function(){
        var text = reader.result;
        star = starCatcher(text);
        if (star != null){
            star.toHTML();
        }
    };

    reader.readAsText(input.files[0]);
};

function openURL(url){
    $("#parser_messages").html("");
    $("#dynamic_anchor").empty().removeClass("star");
    url_data = getFile(url);
    star = starCatcher(url_data);
    if (star != null){
        star.toHTML();
    }
}

function download(filename, text) {
    var element = document.createElement('a');
    element.setAttribute('href', 'data:text/plain;charset=utf-8,' + encodeURIComponent(text));
    element.setAttribute('download', filename);
    element.style.display = 'none';
    document.body.appendChild(element);
    element.click();
    document.body.removeChild(element);
}

function getFile(filename){
    oxmlhttp = null;
    try {
        oxmlhttp = new XMLHttpRequest();
        oxmlhttp.overrideMimeType("text/xml");
    } catch(e){
        try{
            oxmlhttp = new ActiveXObject("Msxml2.XMLHTTP");
        } catch(e){
            return null;
        }
    }
    if(!oxmlhttp) return null;
    try{
        oxmlhttp.open("GET",filename,false);
        oxmlhttp.send(null);
    } catch(e){
        return null;
    }
    return oxmlhttp.responseText;
}

function getURLParameter(name) {
  return decodeURIComponent((new RegExp('[?|&]' + name + '=' + '([^&;]+?)(&|#|;|$)').exec(location.search)||[,""])[1].replace(/\+/g, '%20'))||null
}
