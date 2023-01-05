package com.akabc.ngxmobileclient.data

data class VideoDetails(
    val format: Format? = null,
    val streams: List<Streams?>? = null,
) {
    data class Format(
        val bit_rate: String? = null,
        val duration: String? = null,
        val filename: String? = null,
        val format_long_name: String? = null,
        val format_name: String? = null,
        val nb_programs: Int? = null,
        val nb_streams: Int? = null,
        val probe_score: Int? = null,
        val size: String? = null,
        val start_time: String? = null,
        val tags: Tags? = null,
    ) {
        data class Tags(
            val Hw: String? = null,
            val bitRate: String? = null,
            val comAppleQuickTimeArtwork: String? = null,
            val compatible_brands: String? = null,
            val creation_time: String? = null,
            val encoder: String? = null,
            val major_brand: String? = null,
            val maxrate: String? = null,
            val minor_version: String? = null,
            val te_is_reencode: String? = null,
        )

    }

    data class Streams(
        val avg_frame_rate: String? = null,
        val bit_rate: String? = null,
        val chroma_location: String? = null,
        val closed_captions: Int? = null,
        val codec_long_name: String? = null,
        val codec_name: String? = null,
        val codec_tag: String? = null,
        val codec_tag_string: String? = null,
        val codec_type: String? = null,
        val coded_height: Int? = null,
        val coded_width: Int? = null,
        val color_primaries: String? = null,
        val color_range: String? = null,
        val color_space: String? = null,
        val color_transfer: String? = null,
        val display_aspect_ratio: String? = null,
        val disposition: Disposition? = null,
        val duration: String? = null,
        val duration_ts: Int? = null,
        val has_b_frames: Int? = null,
        val height: Int? = null,
        val index: Int? = null,
        val level: Int? = null,
        val nb_frames: String? = null,
        val pix_fmt: String? = null,
        val profile: String? = null,
        val r_frame_rate: String? = null,
        val refs: Int? = null,
        val sample_aspect_ratio: String? = null,
        val start_pts: Int? = null,
        val start_time: String? = null,
        val tags: Tags? = null,
        val time_base: String? = null,
        val width: Int? = null,
    ) {
        data class Disposition(
            val attached_pic: Int? = null,
            val clean_effects: Int? = null,
            val comment: Int? = null,
            val default: Int? = null,
            val dub: Int? = null,
            val forced: Int? = null,
            val hearing_impaired: Int? = null,
            val karaoke: Int? = null,
            val lyrics: Int? = null,
            val original: Int? = null,
            val timed_thumbnails: Int? = null,
            val visual_impaired: Int? = null,
        )

        data class Tags(
            val creation_time:String? = null,
            val handler_name:String? = null,
            val language:String? = null,
            val vendor_id:String? = null,
        )
    }
}


