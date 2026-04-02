package com.infotech.wishmaplus;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.infotech.wishmaplus.Api.Response.MediaModel;

import java.util.ArrayList;
import java.util.List;

public class MultiSelectBottomSheet extends BottomSheetDialogFragment {

    public interface OnSelectionDoneListener {
        void onDone(List<MediaModel> selectedItems);
    }

    private static final String ARG_MEDIA = "media_list";
    private static final int MAX_SELECT = 10;

    private List<MediaModel> allMedia;
    private final List<MediaModel> selectedItems = new ArrayList<>();
    private OnSelectionDoneListener listener;

    // Views
    private RecyclerView multiSelectRecycler;
    private HorizontalScrollView selectedStripScroll; // see note
    private LinearLayout selectedItemsStrip;
    private LinearLayout selectedPreviewContainer;
    private TextView selectedCountText;
    private TextView doneBtn;
    private TextView clearBtn;
    private TextView selectAllBtn;
    private ImageView closeSheet;

    private MultiSelectAdapter adapter;

    // ─── Factory ──────────────────────────────────────────────────────────────

    public static MultiSelectBottomSheet newInstance(
            ArrayList<MediaModel> mediaList,
            OnSelectionDoneListener listener
    ) {
        MultiSelectBottomSheet sheet = new MultiSelectBottomSheet();
        sheet.listener = listener;
        Bundle args = new Bundle();
        args.putSerializable(ARG_MEDIA, mediaList);
        sheet.setArguments(args);
        return sheet;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setStyle(STYLE_NORMAL, R.style.bottom);
        if (getArguments() != null) {
            allMedia = (List<MediaModel>) getArguments().getSerializable(ARG_MEDIA);
        }
        if (allMedia == null) allMedia = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_multi_select, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Bind views
        multiSelectRecycler = view.findViewById(R.id.multiSelectRecycler);
        selectedItemsStrip = view.findViewById(R.id.selectedItemsStrip);
        selectedPreviewContainer = view.findViewById(R.id.selectedPreviewContainer);
        selectedCountText = view.findViewById(R.id.selectedCountText);
        doneBtn = view.findViewById(R.id.doneBtn);
        clearBtn = view.findViewById(R.id.clearBtn);
        selectAllBtn = view.findViewById(R.id.selectAllBtn);
        closeSheet = view.findViewById(R.id.closeSheet);

        // Setup RecyclerView
        adapter = new MultiSelectAdapter(requireContext(), allMedia, this::onItemToggled);
        multiSelectRecycler.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        multiSelectRecycler.setAdapter(adapter);
        multiSelectRecycler.setNestedScrollingEnabled(true);

        // Expand bottom sheet fully
        expandSheet();

        // Listeners
        closeSheet.setOnClickListener(v -> {
            dismiss();
            selectedItems.clear();
            adapter.clearSelections();
            updateUI();
        });

        clearBtn.setOnClickListener(v -> {
            selectedItems.clear();
            adapter.clearSelections();
            updateUI();
        });

        doneBtn.setOnClickListener(v -> {
            if (selectedItems.isEmpty()) {
                Toast.makeText(requireContext(),
                        "Please select at least one item.",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(requireContext(), ReelEditorActivity.class);
            intent.putExtra("media_list",
                    new ArrayList<>(selectedItems));
            startActivity(intent);
            dismiss();
            selectedItems.clear();
            adapter.clearSelections();
            updateUI();
        });

        selectAllBtn.setOnClickListener(v -> {
            int canSelect = Math.min(allMedia.size(), MAX_SELECT);
            selectedItems.clear();
            for (int i = 0; i < canSelect; i++) {
                allMedia.get(i).setSelected(true);
                allMedia.get(i).setOrder(i + 1);
                selectedItems.add(allMedia.get(i));
            }
            // Deselect rest
            for (int i = canSelect; i < allMedia.size(); i++) {
                allMedia.get(i).setSelected(false);
                allMedia.get(i).setOrder(0);
            }
            adapter.notifyDataSetChanged();
            updateUI();
        });

        updateUI();
    }

    // ─── Item Toggle ──────────────────────────────────────────────────────────

    private void onItemToggled(MediaModel item, int position) {
        if (!item.isSelected()) {
            // Trying to select
            if (selectedItems.size() >= MAX_SELECT) {
                Toast.makeText(requireContext(),
                        "Maximum " + MAX_SELECT + " items can be selected.",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            item.setSelected(true);
            item.setOrder(selectedItems.size() + 1);
            selectedItems.add(item);
        } else {
            // Deselect
            int removedOrder = item.getOrder();
            item.setSelected(false);
            item.setOrder(0);
            selectedItems.remove(item);
            // Re-number remaining
            for (int i = 0; i < selectedItems.size(); i++) {
                selectedItems.get(i).setOrder(i + 1);
            }
        }
        adapter.notifyItemChanged(position);
        updateUI();
    }

    // ─── UI Update ────────────────────────────────────────────────────────────

    @SuppressLint("SetTextI18n")
    private void updateUI() {
        int count = selectedItems.size();

        // Count text
        selectedCountText.setText(count + " selected");

        // Done button
        doneBtn.setText("Next (" + count + ")");
        doneBtn.setAlpha(count > 0 ? 1f : 0.5f);

        // Selected preview strip
        if (count > 0) {
            selectedPreviewContainer.setVisibility(View.VISIBLE);
            rebuildPreviewStrip();
        } else {
            selectedPreviewContainer.setVisibility(View.GONE);
        }

        // Select All label
        selectAllBtn.setText(count >= MAX_SELECT ? "Deselect All" : "Select All");
    }

    private void rebuildPreviewStrip() {
        selectedItemsStrip.removeAllViews();
        int thumbSize = (int) (68 * getResources().getDisplayMetrics().density);
        int margin = (int) (4 * getResources().getDisplayMetrics().density);

        for (int i = 0; i < selectedItems.size(); i++) {
            MediaModel m = selectedItems.get(i);

            FrameLayout frame = new FrameLayout(requireContext());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(thumbSize, thumbSize);
            lp.setMargins(margin, 0, margin, 0);
            frame.setLayoutParams(lp);

            // Thumbnail
            ImageView img = new ImageView(requireContext());
            img.setLayoutParams(new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
            ));
            img.setScaleType(ImageView.ScaleType.CENTER_CROP);
            // Rounded corners via Glide
            Glide.with(this)
                    .load(m.getPath())
                    .transform(new RoundedCorners(16))
                    .into(img);
            frame.addView(img);

            // Order badge
            TextView badge = new TextView(requireContext());
            FrameLayout.LayoutParams bp = new FrameLayout.LayoutParams(
                    (int) (22 * getResources().getDisplayMetrics().density),
                    (int) (22 * getResources().getDisplayMetrics().density)
            );
            bp.gravity = android.view.Gravity.TOP | android.view.Gravity.END;
            bp.topMargin = (int) (3 * getResources().getDisplayMetrics().density);
            bp.rightMargin = (int) (3 * getResources().getDisplayMetrics().density);
            badge.setLayoutParams(bp);
            badge.setText(String.valueOf(m.getOrder()));
            badge.setTextSize(10f);
            badge.setTextColor(0xFFFFFFFF);
            badge.setGravity(android.view.Gravity.CENTER);
            badge.setBackgroundResource(R.drawable.order_badge_bg);
            frame.addView(badge);

            final int pos = i;
            frame.setOnClickListener(v -> {
                // Tap on strip item = deselect
                m.setSelected(false);
                m.setOrder(0);
                selectedItems.remove(m);
                // Re-number
                for (int j = 0; j < selectedItems.size(); j++) {
                    selectedItems.get(j).setOrder(j + 1);
                }
                adapter.notifyDataSetChanged();
                updateUI();
            });

            selectedItemsStrip.addView(frame);
        }
    }

    // ─── Sheet Expansion ──────────────────────────────────────────────────────

    private void expandSheet() {
        // Make bottom sheet expand to full screen
        if (getDialog() != null) {
            getDialog().setOnShowListener(d -> {
                FrameLayout bottomSheet = getDialog().findViewById(
                        com.google.android.material.R.id.design_bottom_sheet
                );
                if (bottomSheet != null) {
                    BottomSheetBehavior<FrameLayout> behavior = BottomSheetBehavior.from(bottomSheet);
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    behavior.setSkipCollapsed(true);
                    // Let it go full height
                    ViewGroup.LayoutParams params = bottomSheet.getLayoutParams();
                    params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    bottomSheet.setLayoutParams(params);
                }
            });
        }
    }
}
