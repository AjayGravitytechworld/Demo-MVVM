package com.loancalc.smartloan.emicalc.calculator.Frags;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.loancalc.smartloan.emicalc.calculator.ADS.Ads;
import com.loancalc.smartloan.emicalc.calculator.Activity.AfordCalc_Sc;
import com.loancalc.smartloan.emicalc.calculator.Activity.BMICalc_Sc;
import com.loancalc.smartloan.emicalc.calculator.Activity.BloodSugarCalc_Sc;
import com.loancalc.smartloan.emicalc.calculator.Activity.CalorieCalc_Sc;
import com.loancalc.smartloan.emicalc.calculator.Activity.FDRDCalc_Sc;
import com.loancalc.smartloan.emicalc.calculator.Activity.FinCalc_Sc;
import com.loancalc.smartloan.emicalc.calculator.Activity.GstCalc_Sc;
import com.loancalc.smartloan.emicalc.calculator.Activity.LumpsumCalc_Sc;
import com.loancalc.smartloan.emicalc.calculator.Activity.PrepayCalc_Sc;
import com.loancalc.smartloan.emicalc.calculator.Activity.RentBuyCalc_Sc;
import com.loancalc.smartloan.emicalc.calculator.Activity.SipCalc_Sc;
import com.loancalc.smartloan.emicalc.calculator.Activity.WaterIntakeCalc_Sc;
import com.loancalc.smartloan.emicalc.calculator.Activity.WordCountCalc_Sc;
import com.loancalc.smartloan.emicalc.calculator.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {
    FragmentHomeBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        binding.financecalc.setOnClickListener(v -> Ads.SHowInter(getActivity(), () -> startActivity(new Intent(getContext(), FinCalc_Sc.class))));

        binding.sipcalc.setOnClickListener(v -> Ads.SHowInter(getActivity(), () -> startActivity(new Intent(getContext(), SipCalc_Sc.class))));

        binding.gstcalc.setOnClickListener(v -> Ads.SHowInter(getActivity(), () -> startActivity(new Intent(getContext(), GstCalc_Sc.class))));

        binding.fdrd.setOnClickListener(v -> Ads.SHowInter(getActivity(), () -> startActivity(new Intent(getContext(), FDRDCalc_Sc.class))));

        binding.lumpsum.setOnClickListener(v -> Ads.SHowInter(getActivity(), () -> startActivity(new Intent(getContext(), LumpsumCalc_Sc.class))));

        binding.caniaford.setOnClickListener(v -> Ads.SHowInter(getActivity(), () -> startActivity(new Intent(getContext(), AfordCalc_Sc.class))));

        binding.rentNbuy.setOnClickListener(v -> Ads.SHowInter(getActivity(), () -> startActivity(new Intent(getContext(), RentBuyCalc_Sc.class))));

        binding.bmicalc.setOnClickListener(v -> Ads.SHowInter(getActivity(), () -> startActivity(new Intent(getContext(), BMICalc_Sc.class))));

        binding.bloodsugar.setOnClickListener(v -> Ads.SHowInter(getActivity(), () -> startActivity(new Intent(getContext(), BloodSugarCalc_Sc.class))));

        binding.calories.setOnClickListener(v -> Ads.SHowInter(getActivity(), () -> startActivity(new Intent(getContext(), CalorieCalc_Sc.class))));

        binding.waterintake.setOnClickListener(v -> Ads.SHowInter(getActivity(), () -> startActivity(new Intent(getContext(), WaterIntakeCalc_Sc.class))));

        binding.wordcounter.setOnClickListener(v -> Ads.SHowInter(getActivity(), () -> startActivity(new Intent(getContext(), WordCountCalc_Sc.class))));

        binding.prepayment.setOnClickListener(v -> Ads.SHowInter(getActivity(), () -> startActivity(new Intent(getContext(), PrepayCalc_Sc.class))));

        return binding.getRoot();
    }
}
