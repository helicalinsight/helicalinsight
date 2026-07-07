
const Base64 = {

    // private property
    _keyStr : "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=",

    // public method for encoding
    encode : function (input) {
        let output = "";
        let chr1, chr2, chr3, enc1, enc2, enc3, enc4;
        let i = 0;

        input = Base64._utf8_encode(input);

        while (i < input.length) {

            chr1 = input.charCodeAt(i++);
            chr2 = input.charCodeAt(i++);
            chr3 = input.charCodeAt(i++);

            enc1 = chr1 >> 2;
            enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
            enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
            enc4 = chr3 & 63;

            if (isNaN(chr2)) {
                enc3 = enc4 = 64;
            } else if (isNaN(chr3)) {
                enc4 = 64;
            }

            output = output +
            this._keyStr.charAt(enc1) + this._keyStr.charAt(enc2) +
            this._keyStr.charAt(enc3) + this._keyStr.charAt(enc4);

        }

        return output.replace(/\\u0000/g, "");
    },

    // public method for decoding
    decode : function (input) {
        let output = "";
        let chr1, chr2, chr3;
        let enc1, enc2, enc3, enc4;
        let i = 0;

        input = input.replace(/[^A-Za-z0-9\\+\\/\\=]/g, "");

        while (i < input.length) {

            enc1 = this._keyStr.indexOf(input.charAt(i++));
            enc2 = this._keyStr.indexOf(input.charAt(i++));
            enc3 = this._keyStr.indexOf(input.charAt(i++));
            enc4 = this._keyStr.indexOf(input.charAt(i++));

            chr1 = (enc1 << 2) | (enc2 >> 4);
            chr2 = ((enc2 & 15) << 4) | (enc3 >> 2);
            chr3 = ((enc3 & 3) << 6) | enc4;

            output = output + String.fromCharCode(chr1);

            if (enc3 !== 64) {
                output = output + String.fromCharCode(chr2);
            }
            if (enc4 !== 64) {
                output = output + String.fromCharCode(chr3);
            }

        }

        output = Base64._utf8_decode(output);

        return output.replace(/\\u0000/g, "");

    },

    // private method for UTF-8 encoding
    _utf8_encode : function (string) {
        string = string.replace(/\r\n/g,"\n");
        let utftext = "";

        for (let n = 0; n < string.length; n++) {

            let baceC = string.charCodeAt(n);

            if (baceC < 128) {
                utftext += String.fromCharCode(baceC);
            }
            else if((baceC > 127) && (baceC < 2048)) {
                utftext += String.fromCharCode((baceC >> 6) | 192);
                utftext += String.fromCharCode((baceC & 63) | 128);
            }
            else {
                utftext += String.fromCharCode((baceC >> 12) | 224);
                utftext += String.fromCharCode(((baceC >> 6) & 63) | 128);
                utftext += String.fromCharCode((baceC & 63) | 128);
            }

        }

        return utftext;
    },

    // private method for UTF-8 decoding
    _utf8_decode : function (utftext) {
        let string = "";
        let i = 0;
        let baceC = 0;
        let baceC3 = 0;
        let baceC2 = 0;

        while ( i < utftext.length ) {

            baceC = utftext.charCodeAt(i);

            if (baceC < 128) {
                string += String.fromCharCode(baceC);
                i++;
            }
            else if((baceC > 191) && (baceC < 224)) {
                baceC2 = utftext.charCodeAt(i+1);
                string += String.fromCharCode((( baceC & 31) << 6) | (baceC2 & 63));
                i += 2;
            }
            else {
                baceC2 = utftext.charCodeAt(i+1);
                baceC3 = utftext.charCodeAt(i+2);
                string += String.fromCharCode(((baceC & 15) << 12) | ((baceC2 & 63) << 6) | (baceC3 & 63));
                i += 3;
            }

        }

        return string;
    }

}

export default Base64;
